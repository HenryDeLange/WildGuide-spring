package mywild.wildguide.guide.web;

import java.util.List;
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
import mywild.wildguide.framework.security.jwt.JwtUtils;
import mywild.wildguide.framework.web.Paged;
import mywild.wildguide.guide.logic.GuideService;

@Tag(name = "Guides", description = "Manage Guides.")
@RestController
public class GuideController {

    @Autowired
    private GuideService service;
    
    @Operation(summary = "Find all Guides associated with the User (owner, member or public).")
    @GetMapping("/guides")
    public Paged<Guide> findGuides(
        JwtAuthenticationToken jwtToken,
        @RequestParam(defaultValue = "0") int page
    ) {
        return service.findGuides(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            page);
    }

    @Operation(summary = "Find a specific Guide associated with the User (owner, member or public).")
    @GetMapping("/guides/{guideId}")
    public Guide findGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId
    ) {
        return service.findGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId);
    }

    @Operation(summary = "Create a new Guide.")
    @PostMapping("/guides")
    public Guide createGuide(
        JwtAuthenticationToken jwtToken,
        @RequestBody GuideBase dto
    ) {
        return service.createGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            dto);
    }

    @Operation(summary = "Update a specific Guide.")
    @PutMapping("/guides/{guideId}")
    public Guide updateGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @RequestBody GuideBase dto
    ) {
        return service.updateGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId, 
            dto);
    }

    @Operation(summary = "Delete a specific Guide.")
    @DeleteMapping("/guides/{guideId}")
    public void deleteGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId
    ) {
        service.deleteGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId);
    }

    @Operation(summary = "Find the Owners for a specific Guide.")
    @GetMapping("/guides/{guideId}/owners")
    public List<Long> findGuideOwners(
        @PathVariable long guideId
    ) {
        return service.findGuideOwners(
            guideId);
    }

    @Operation(summary = "Join a specific Guide as an Owner.")
    @PostMapping("/guides/{guideId}/owners/{ownerId}")
    public boolean ownerJoinGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long ownerId
    ) {
        return service.ownerJoinGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId, 
            ownerId);
    }

    @Operation(summary = "Leave a specific Guide as an Owner.")
    @DeleteMapping("/guides/{guideId}/owners/{ownerId}")
    public boolean ownerLeaveGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long ownerId
    ) {
        return service.ownerLeaveGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId, 
            ownerId);
    }

    @Operation(summary = "Find the Members for a specific Guide.")
    @GetMapping("/guides/{guideId}/members")
    public List<Long> findGuideMembers(
        @PathVariable long guideId
    ) {
        return service.findGuideMembers(
            guideId);
    }

    @Operation(summary = "Join a specific Guide as a Member.")
    @PostMapping("/guides/{guideId}/members/{memberId}")
    public boolean memberJoinGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId,
        @PathVariable long memberId
    ) {
        return service.memberJoinGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId, 
            memberId);
    }

    @Operation(summary = "Leave a specific Guide as a Member.")
    @DeleteMapping("/guides/{guideId}/members/{memberId}")
    public boolean memberLeaveGuide(
        JwtAuthenticationToken jwtToken,
        @PathVariable long guideId, 
        @PathVariable long memberId
    ) {
        return service.memberLeaveGuide(
            JwtUtils.getUserIdFromJwt(jwtToken), 
            guideId, 
            memberId);
    }

}