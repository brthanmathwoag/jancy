package eu.tznvy.jancy.core;

import eu.tznvy.jancy.core.helpers.ArraysHelper;

public class Configuration {
    private final Inventory[] inventories;
    private final Playbook[] playbooks;
    private final Role[] roles;

    public Configuration() {
        this.inventories = new Inventory[0];
        this.playbooks = new Playbook[0];
        this.roles = new Role[0];
    }

    private Configuration(Inventory[] inventories, Playbook[] playbooks, Role[] roles) {
        this.inventories = inventories;
        this.playbooks = playbooks;
        this.roles = roles;
    }

    public Configuration inventories(Inventory... inventories) {
        return new Configuration(
                ArraysHelper.copyIfNotEmpty(inventories),
                this.playbooks,
                this.roles);
    }

    public Configuration playbooks(Playbook... playbooks) {
        return new Configuration(
                this.inventories,
                ArraysHelper.copyIfNotEmpty(playbooks),
                this.roles);
    }

    public Configuration roles(Role... roles) {
        return new Configuration(
                this.inventories,
                this.playbooks,
                ArraysHelper.copyIfNotEmpty(roles));
    }

    public Inventory[] getInventories() {
        return ArraysHelper.copyIfNotEmpty(this.inventories);
    }

    public Playbook[] getPlaybooks() {
        return ArraysHelper.copyIfNotEmpty(this.playbooks);
    }

    public Role[] getRoles() {
        return ArraysHelper.copyIfNotEmpty(this.roles);
    }
}
