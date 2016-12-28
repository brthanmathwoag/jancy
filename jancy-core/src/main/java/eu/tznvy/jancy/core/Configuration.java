package eu.tznvy.jancy.core;

import eu.tznvy.jancy.core.helpers.ArraysHelper;

public class Configuration {
    private final Inventory[] inventories;
    private final String name;
    private final Playbook[] playbooks;
    private final Role[] roles;

    public Configuration(String name) {
        this.inventories = new Inventory[0];
        this.name = name;
        this.playbooks = new Playbook[0];
        this.roles = new Role[0];
    }

    private Configuration(Inventory[] inventories, String name, Playbook[] playbooks, Role[] roles) {
        this.inventories = inventories;
        this.playbooks = playbooks;
        this.roles = roles;
        this.name = name;
    }

    public Configuration inventories(Inventory... inventories) {
        return new Configuration(
                ArraysHelper.copyIfNotEmpty(inventories),
                this.name,
                this.playbooks,
                this.roles);
    }

    public Configuration playbooks(Playbook... playbooks) {
        return new Configuration(
                this.inventories,
                this.name,
                ArraysHelper.copyIfNotEmpty(playbooks),
                this.roles);
    }

    public Configuration roles(Role... roles) {
        return new Configuration(
                this.inventories,
                this.name,
                this.playbooks,
                ArraysHelper.copyIfNotEmpty(roles));
    }

    public Inventory[] getInventories() {
        return ArraysHelper.copyIfNotEmpty(this.inventories);
    }

    public String getName() {
        return this.name;
    }

    public Playbook[] getPlaybooks() {
        return ArraysHelper.copyIfNotEmpty(this.playbooks);
    }

    public Role[] getRoles() {
        return ArraysHelper.copyIfNotEmpty(this.roles);
    }
}
