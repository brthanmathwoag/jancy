package eu.tznvy.jancy.core;

import eu.tznvy.jancy.core.helpers.ArraysHelper;

public class Playbook {
    private final Inventory[] inventories;
    private final String name;
    private final Play[] plays;
    private final Role[] roles;

    public Playbook(String name) {
        this.inventories = new Inventory[0];
        this.name = name;
        this.plays = new Play[0];
        this.roles = new Role[0];
    }

    private Playbook(Inventory[] inventories, String name, Play[] plays, Role[] roles) {
        this.inventories = inventories;
        this.plays = plays;
        this.roles = roles;
        this.name = name;
    }

    public Playbook inventories(Inventory... inventories) {
        return new Playbook(
                ArraysHelper.copyIfNotEmpty(inventories),
                this.name,
                this.plays,
                this.roles);
    }

    public Playbook plays(Play... plays) {
        return new Playbook(
                this.inventories,
                this.name,
                ArraysHelper.copyIfNotEmpty(plays),
                this.roles);
    }

    public Playbook roles(Role... roles) {
        return new Playbook(
                this.inventories,
                this.name,
                this.plays,
                ArraysHelper.copyIfNotEmpty(roles));
    }

    public Inventory[] getInventories() {
        return ArraysHelper.copyIfNotEmpty(this.inventories);
    }

    public String getName() {
        return this.name;
    }

    public Play[] getPlays() {
        return ArraysHelper.copyIfNotEmpty(this.plays);
    }

    public Role[] getRoles() {
        return ArraysHelper.copyIfNotEmpty(this.roles);
    }
}
