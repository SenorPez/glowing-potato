package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars/{starId}/planets/{planetId}/calendars/{calendarId}/festivalYear",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_UTF8_VALUE}
)
@RestController
class FestivalYearController {
    private final APIService apiService;

    @Autowired
    FestivalYearController(APIService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping(value = "/{localYear}")
    ResponseEntity<FestivalYearResource> festivalYear(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId,
            @PathVariable final Integer localYear) {
        final FestivalYear festivalYear = new FestivalYear(localYear);
        final FestivalYearModel festivalYearModel = new FestivalYearModel(festivalYear);
        final FestivalYearResource festivalYearResource = festivalYearModel.toResource(solarSystemId, starId, planetId, calendarId);
        return ResponseEntity.ok(festivalYearResource);
    }
}
