package com.nikza.socialnetwork.facade;

import com.nikza.socialnetwork.dto.CommunityDTO;
import com.nikza.socialnetwork.entity.Community;
import org.springframework.stereotype.Component;

@Component
public class CommunityFacade {
    public CommunityDTO communityToCommunityDTO(Community community){
        CommunityDTO communityDTO = new CommunityDTO();
        communityDTO.setId(community.getId());
        communityDTO.setName(community.getName());
        communityDTO.setDescription(community.getDescription());
        return communityDTO;
    }
}
