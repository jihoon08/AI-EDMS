package com.edms.folder.service;

import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import com.edms.folder.domain.Folder;
import com.edms.folder.dto.FolderDto;
import com.edms.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional(readOnly = true)
    public List<FolderDto.TreeNode> getFolderTree() {
        List<Folder> allFolders = folderRepository.findAllActive();
        return buildTree(allFolders);
    }

    @Transactional(readOnly = true)
    public List<FolderDto.Response> getChildren(UUID parentUuid) {
        List<Folder> children = parentUuid != null
                ? folderRepository.findByParentUuid(parentUuid)
                : folderRepository.findRootFolders();
        return children.stream().map(FolderDto.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public FolderDto.Response getFolder(UUID folderUuid) {
        Folder folder = findActiveFolder(folderUuid);
        return FolderDto.Response.from(folder);
    }

    @Transactional
    public FolderDto.Response createFolder(FolderDto.CreateRequest request, UUID userUuid) {
        String path;
        int depth;

        if (request.getParentUuid() != null) {
            Folder parent = findActiveFolder(request.getParentUuid());
            path = parent.getMaterializedPath() + parent.getFolderUuid() + "/";
            depth = parent.getDepth() + 1;
        } else {
            path = "/";
            depth = 0;
        }

        Folder folder = Folder.builder()
                .folderName(request.getFolderName())
                .parentUuid(request.getParentUuid())
                .materializedPath(path)
                .depth(depth)
                .description(request.getDescription())
                .ownerUuid(userUuid)
                .build();
        folder.setCreatedByUuid(userUuid);

        folderRepository.save(folder);
        log.info("폴더 생성: {} ({})", folder.getFolderName(), folder.getFolderUuid());
        return FolderDto.Response.from(folder);
    }

    @Transactional
    public FolderDto.Response updateFolder(UUID folderUuid, FolderDto.UpdateRequest request, UUID userUuid) {
        Folder folder = findActiveFolder(folderUuid);
        folder.rename(request.getFolderName());
        folder.setUpdatedByUuid(userUuid);
        return FolderDto.Response.from(folder);
    }

    @Transactional
    public void deleteFolder(UUID folderUuid, UUID userUuid) {
        Folder folder = findActiveFolder(folderUuid);
        long childCount = folderRepository.countChildren(folderUuid);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.FOLDER_HAS_CHILDREN);
        }
        folder.softDelete();
        folder.setUpdatedByUuid(userUuid);
        log.info("폴더 삭제: {} ({})", folder.getFolderName(), folderUuid);
    }

    private Folder findActiveFolder(UUID folderUuid) {
        return folderRepository.findActiveById(folderUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.FOLDER_NOT_FOUND));
    }

    private List<FolderDto.TreeNode> buildTree(List<Folder> folders) {
        Map<UUID, FolderDto.TreeNode> nodeMap = new LinkedHashMap<>();
        List<FolderDto.TreeNode> roots = new ArrayList<>();

        for (Folder f : folders) {
            nodeMap.put(f.getFolderUuid(), FolderDto.TreeNode.from(f));
        }

        for (Folder f : folders) {
            FolderDto.TreeNode node = nodeMap.get(f.getFolderUuid());
            if (f.getParentUuid() != null && nodeMap.containsKey(f.getParentUuid())) {
                nodeMap.get(f.getParentUuid()).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        return roots;
    }
}
