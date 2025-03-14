package mywild.wildguide.domain.entry.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mywild.wildguide.domain.entry.logic.EntryService;
import mywild.wildguide.framework.security.jwt.JwtUtils;
import mywild.wildguide.framework.web.BaseController;
import mywild.wildguide.framework.web.Paged;

@Tag(name = "Entries", description = "Manage Guide Entries.")
@RestController
public class EntryController extends BaseController {

    @Autowired
    private EntryService service;
    
    @Operation(summary = "Find all Entries associated with the Guide.")
    @GetMapping("/guides/{guideId}/entries")
    public Paged<Entry> findEntries(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String name
    ) {
        return service.findEntries(
            JwtUtils.getUserIdFromJwt(jwtToken),
            guideId,
            page,
            name);
    }

    @Operation(summary = "Find a specific Entry associated with the specified Guide.")
    @GetMapping("/guides/{guideId}/entries/{entryId}")
    public Entry findEntry(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long entryId
    ) {
        return service.findEntry(
            JwtUtils.getUserIdFromJwt(jwtToken),
            guideId,
            entryId);
    }

    @Operation(summary = "Create a new Entry associated with the specified Guide.")
    @PostMapping("/guides/{guideId}/entries")
    public Entry createEntry(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @RequestBody EntryBase dto
    ) {
        return service.createEntry(
            JwtUtils.getUserIdFromJwt(jwtToken),
            guideId,
            dto);
    }

    @Operation(summary = "Update a specific Entry associated with the specified Guide.")
    @PutMapping("/guides/{guideId}/entries/{entryId}")
    public Entry updateEntry(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long entryId,
        @RequestBody EntryBase dto
    ) {
        return service.updateEntry(
            JwtUtils.getUserIdFromJwt(jwtToken),
            guideId,
            entryId,
            dto);
    }

    @Operation(summary = "Delete a specific Entry associated with the specified Guide.")
    @DeleteMapping("/guides/{guideId}/entries/{entryId}")
    public void deleteEntry(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long entryId
    ) {
        service.deleteEntry(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId,
            entryId);
    }

}
