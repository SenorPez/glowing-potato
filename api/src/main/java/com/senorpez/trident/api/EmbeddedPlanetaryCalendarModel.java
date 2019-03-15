package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(value = "calendar", collectionRelation = "calendar")
public class EmbeddedPlanetaryCalendarModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    public EmbeddedPlanetaryCalendarModel(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();
        this.name = planetaryCalendar.getName();
    }

    Resource<EmbeddedPlanetaryCalendarModel> toResource(final int solarSystemId, final int starId, final int planetId) {
        final APIEmbeddedResourceAssembler<EmbeddedPlanetaryCalendarModel, EmbeddedPlanetaryCalendarResource> assembler = new APIEmbeddedResourceAssembler<EmbeddedPlanetaryCalendarModel, EmbeddedPlanetaryCalendarResource>(PlanetaryCalendarController.class, EmbeddedPlanetaryCalendarResource.class, () -> new EmbeddedPlanetaryCalendarResource(this, solarSystemId, starId, planetId));
        return assembler.toResource(this, solarSystemId, starId, planetId);
    }

    private class EmbeddedPlanetaryCalendarResource extends Resource<EmbeddedPlanetaryCalendarModel> {
        private EmbeddedPlanetaryCalendarResource(final EmbeddedPlanetaryCalendarModel content, final int solarSystemId, final int starId, final int planetId, final Link... links) {
            super(content, links);
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
