package com.edms.folder.dto;

import com.edms.folder.domain.Folder;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FolderDto {

    @Getter
    @Builder
    public static class Response {
        private UUID folderUuid;
        private UUID parentUuid;
        private String folderName;
        private String materializedPath;
        private Integer depth;
        private String description;
        private UUID ownerUuid;
        private Integer sortOrder;

        public static Response from(Folder folder) {
            return Response.builder()
                    .folderUuid(folder.getFolderUuid())
                    .parentUuid(folder.getParentUuid())
                    .folderName(folder.getFolderName())
                    .materializedPath(folder.getMaterializedPath())
                    .depth(folder.getDepth())
                    .description(folder.getDescription())
                    .ownerUuid(folder.getOwnerUuid())
                    .sortOrder(folder.getSortOrder())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TreeNode {
        private UUID folderUuid;
        private String folderName;
        private Integer depth;
        @Builder.Default
        private List<TreeNode> children = new ArrayList<>();

        public static TreeNode from(Folder folder) {
            return TreeNode.builder()
                    .folderUuid(folder.getFolderUuid())
                    .folderName(folder.getFolderName())
                    .depth(folder.getDepth())
                    .build();
        }
    }

    @Getter
    public static class CreateRequest {
        @NotBlank(message = "폴더 이름을 입력해주세요")
        private String folderName;
        private UUID parentUuid;
        private String description;
    }

    @Getter
    public static class UpdateRequest {
        @NotBlank(message = "폴더 이름을 입력해주세요")
        private String folderName;
        private String description;
    }
}
