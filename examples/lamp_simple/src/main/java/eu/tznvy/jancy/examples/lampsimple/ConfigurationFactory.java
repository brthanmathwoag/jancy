package eu.tznvy.jancy.examples.lampsimple;


import eu.tznvy.jancy.core.*;
import eu.tznvy.jancy.modules.commands.Command;
import eu.tznvy.jancy.modules.database.mysql.MysqlDb;
import eu.tznvy.jancy.modules.database.mysql.MysqlUser;
import eu.tznvy.jancy.modules.files.Acl;
import eu.tznvy.jancy.modules.files.Lineinfile;
import eu.tznvy.jancy.modules.files.Template;
import eu.tznvy.jancy.modules.packaging.os.Yum;
import eu.tznvy.jancy.modules.sourcecontrol.Git;
import eu.tznvy.jancy.modules.system.Seboolean;
import eu.tznvy.jancy.modules.system.Service;

import java.util.HashMap;

public class ConfigurationFactory implements eu.tznvy.jancy.core.ConfigurationFactory {
    @Override
    public Configuration build() {
        Group webservers = new Group("webservers")
                .hosts(new Host("web3"));

        HashMap<String, Object> dbVars = new HashMap<>();
        dbVars.put("mysqlservice", "mysqld");
        dbVars.put("mysql_port", 3306);
        dbVars.put("dbuser", "foouser");
        dbVars.put("dbname", "foodb");
        dbVars.put("upassword", "abc");

        Group dbservers = new Group("dbservers")
            .hosts(new Host("web2"))
            .vars(dbVars);

        HashMap<String, Object> allVars = new HashMap<>();
        allVars.put("httpd_port", 89);
        allVars.put("ntpserver", "192.168.1.2");
        allVars.put("repository", "https://github.com/bennojoy/mywebapp.git");

        Inventory inventory = new Inventory("hosts")
            .groups(webservers, dbservers)
            .vars(allVars);

        Handler restartNtp = new Handler("restart ntp")
            .action(new Service()
                .name("ntpd")
                .state(Service.State.RESTARTED));

        Handler restartIptables = new Handler("restart iptables")
            .action(new Service()
                .name("iptables")
                .state(Service.State.RESTARTED));

        Handler restartMysql = new Handler("restart mysql")
            .action(new Service()
                .name("mysqld")
                .state(Service.State.RESTARTED));

        Role common = new Role("common")
            .handlers(restartNtp, restartIptables)
            .tasks(
                new Task("Install ntp")
                    .action(new Yum()
                        .name("ntp")
                        .state(Yum.State.PRESENT))
                    .tags("ntp"),
                new Task("Configure ntp file")
                    .action(new Template()
                        .src("ntp.conf.j2")
                        .dest("/etc/ntp.conf"))
                    .tags("ntp")
                    .notify(restartNtp),
                new Task("Start the ntp service")
                    .action(new Service()
                        .name("ntpd")
                        .state(Service.State.STARTED)
                        .enabled(true))
                    .tags("ntp"),
                new Task("Test to see if selinux is running")
                    .action(new Command()
                        .freeForm("getenforce"))
                    .register("sestatus")
                    //.changedWhen("false")
            );

        Role web = new Role("web")
            .handlers(restartIptables)
            .tasks(
                //included from Install_httpd.yml
                new Task("Install http and php etc")
                    .action(new Yum()
                        .name("{{ item }}")
                        .state(Yum.State.PRESENT))
                    .withItems(
                       "httpd",
                        "php",
                        "php-mysql",
                        "git",
                        "libsemanage-python",
                        "libselinux-python"),
                new Task("insert iptables rule for httpd")
                    .action(new Lineinfile()
                        .dest("/etc/sysconfig/iptables")
                        .create(true)
                        .state(Lineinfile.State.PRESENT)
                        .regexp("{{ httpd_port }}")
                        .insertafter("^:OUTPUT ")
                        .line("-A INPUT -p tcp  --dport {{ httpd_port }} -j  ACCEPT"))
                    .notify(restartIptables),
                new Task("http service state")
                    .action(new Service()
                        .name("httpd")
                        .state(Service.State.STARTED)
                        .enabled(true)),
                new Task("Configure SELinux to allow httpd to connect to remote database")
                    .action(new Seboolean()
                        .name("httpd_can_network_connect_db")
                        .state(true)
                        .persistent(true))
                    .when("sestatus.rc != 0"),
                //included from copy_code.yml
                new Task("Copy the code from repository")
                    .action(new Git()
                        .repo("{{ repository }}")
                        .dest("Copy the code from repository")),
                new Task("Creates the index.php file")
                    .action(new Template()
                        .src("index.php.j2")
                        .dest("/var/www/html/index.php"))
            );

        Role db = new Role("db")
            .handlers(restartMysql, restartIptables)
            .tasks(
                new Task("Install Mysql package")
                    .action(new Yum()
                        .name("{{ item }}")
                        .state(Yum.State.INSTALLED))
                    .withItems(
                        "mysql-server",
                        "MySQL-python",
                        "libselinux-python",
                        "libsemanage-python"),
                new Task("Configure SELinux to start mysql on any port")
                    .action(new Seboolean()
                        .name("mysql_connect_any")
                        .state(true)
                        .persistent(true))
                    .when("sestatus.rc != 0"),
                new Task("Create Mysql configuration file")
                    .action(new Template()
                        .src("my.cnf.j2")
                        .dest("/etc/my.cnf"))
                    .notify(restartMysql),
                new Task("Start Mysql Service")
                    .action(new Service()
                        .name("mysqld")
                        .state(Service.State.STARTED)
                        .enabled(true)),
                new Task("insert iptables rule")
                    .action(new Lineinfile()
                        .dest("/etc/sysconfig/iptables")
                        .state(Lineinfile.State.PRESENT)
                        .regexp("{{ mysql_port }}")
                        .insertafter("^:OUTPUT ")
                        .line("-A INPUT -p tcp  --dport {{ mysql_port }} -j  ACCEPT"))
                    .notify(restartIptables),
                new Task("Create Application Database")
                    .action(new MysqlDb()
                        .name("{{ dbname }}")
                        .state(MysqlDb.State.PRESENT)),
                new Task("Create Application DB User")
                    .action(new MysqlUser()
                        .name("{{ dbuser }}")
                        .password("{{ upassword }}")
                        .priv("*.*:ALL")
                        .host("%")
                        .state(MysqlUser.State.PRESENT))
            );

        Playbook commonPlay = new Playbook("apply common configuration to all nodes")
            .hosts(new Host("all"))
            .roles(common)
            //.remoteUser("root")
        ;

        Playbook webPlay = new Playbook("configure and deploy the webservers and application code")
            .hosts(webservers)
            .roles(web)
            //.remoteUser("root")
        ;

        Playbook dbPlay = new Playbook("deploy MySQL and configure the databases")
            .hosts(dbservers)
            .roles(db)
            //.remoteUser("root")
        ;

        return new Configuration("lamp_simple")
            .inventories(inventory)
            .roles(common, web, db)
            .playbooks(commonPlay, webPlay, dbPlay);
    }
}
