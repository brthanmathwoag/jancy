package eu.tznvy.jancy.examples.lampsimple;


import eu.tznvy.jancy.core.*;
import eu.tznvy.jancy.modules.commands.Command;
import eu.tznvy.jancy.modules.database.mysql.MysqlDb;
import eu.tznvy.jancy.modules.database.mysql.MysqlUser;
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

        Role common = new Role("common")
            .handlers(
                new Handler("restart ntp")
                    .action(new Service()
                        .name("ntpd")
                        .state("restarted")),
                new Handler("restart iptables")
                    .action(new Service()
                        .name("iptables")
                        .state("restarted"))
            )
            .tasks(
                new Task("Install ntp")
                    .action(new Yum()
                        .name("ntp")
                        .state("present"))
                    .tags("ntp"),
                new Task("Configure ntp file")
                    .action(new Template()
                        .src("ntp.conf.j2")
                        .dest("/etc/ntp.conf"))
                    .tags("ntp")
                    .notify("restart ntp"),
                new Task("Start the ntp service")
                    .action(new Service()
                        .name("ntpd")
                        .state("started")
                        .enabled("yes"))
                    .tags("ntp"),
                new Task("Test to see if selinux is running")
                    .action(new Command()
                        .freeForm("getenforce"))
                    .register("sestatus")
                    //.changedWhen("false")
            );

        Role web = new Role("web")
            .tasks(
                //included from Install_httpd.yml
                new Task("Install http and php etc")
                    .action(new Yum()
                        .name("{{ item }}")
                        .state("present"))
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
                        .create("yes")
                        .state("present")
                        .regexp("{{ httpd_port }}")
                        .insertafter("^:OUTPUT ")
                        .line("-A INPUT -p tcp  --dport {{ httpd_port }} -j  ACCEPT"))
                    .notify("restart iptables"),
                new Task("http service state")
                    .action(new Service()
                        .name("httpd")
                        .state("started")
                        .enabled("yes")),
                new Task("Configure SELinux to allow httpd to connect to remote database")
                    .action(new Seboolean()
                        .name("httpd_can_network_connect_db")
                        .state("true")
                        .persistent("yes"))
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
            )
            .handlers(
                new Handler("restart iptables")
                    .action(new Service()
                        .name("iptables")
                        .state("restarted"))
            );

        Role db = new Role("db")
            .tasks(
                new Task("Install Mysql package")
                    .action(new Yum()
                        .name("{{ item }}")
                        .state("installed"))
                    .withItems(
                        "mysql-server",
                        "MySQL-python",
                        "libselinux-python",
                        "libsemanage-python"),
                new Task("Configure SELinux to start mysql on any port")
                    .action(new Seboolean()
                        .name("mysql_connect_any")
                        .state("true")
                        .persistent("yes"))
                    .when("sestatus.rc != 0"),
                new Task("Create Mysql configuration file")
                    .action(new Template()
                        .src("my.cnf.j2")
                        .dest("/etc/my.cnf"))
                    .notify("restart mysql"),
                new Task("Start Mysql Service")
                    .action(new Service()
                        .name("mysqld")
                        .state("started")
                        .enabled("yes")),
                new Task("insert iptables rule")
                    .action(new Lineinfile()
                        .dest("/etc/sysconfig/iptables")
                        .state("present")
                        .regexp("{{ mysql_port }}")
                        .insertafter("^:OUTPUT ")
                        .line("-A INPUT -p tcp  --dport {{ mysql_port }} -j  ACCEPT"))
                    .notify("restart iptables"),
                new Task("Create Application Database")
                    .action(new MysqlDb()
                        .name("{{ dbname }}")
                        .state("present")),
                new Task("Create Application DB User")
                    .action(new MysqlUser()
                        .name("{{ dbuser }}")
                        .password("{{ upassword }}")
                        .priv("*.*:ALL")
                        .host("%")
                        .state("present"))
            )
            .handlers(
                new Handler("restart mysql")
                    .action(new Service()
                        .name("mysqld")
                        .state("restarted")),
                new Handler("restart iptables")
                    .action(new Service()
                        .name("iptables")
                        .state("restarted"))
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