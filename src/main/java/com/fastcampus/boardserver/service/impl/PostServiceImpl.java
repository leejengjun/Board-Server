package com.fastcampus.boardserver.service.impl;

import com.fastcampus.boardserver.dto.CommentDTO;
import com.fastcampus.boardserver.dto.PostDTO;
import com.fastcampus.boardserver.dto.TagDTO;
import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.exception.BoardServerException;
import com.fastcampus.boardserver.mapper.CommentMapper;
import com.fastcampus.boardserver.mapper.PostMapper;
import com.fastcampus.boardserver.mapper.TagMapper;
import com.fastcampus.boardserver.mapper.UserProfileMapper;
import com.fastcampus.boardserver.service.PostService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public void register(String id, PostDTO postDTO) {
        UserDTO memberInfo = userProfileMapper.getUserProfile(id);
        postDTO.setUserId(memberInfo.getId());
        postDTO.setCreateTime(new Date());

        if (memberInfo != null) {
            try {
                postMapper.register(postDTO);
                Integer postId = postDTO.getId();
                // 생성된 post 객체 에서 태그 리스트 생성
                for(int i=0; i<postDTO.getTagDTOList().size(); i++){
                    TagDTO tagDTO = postDTO.getTagDTOList().get(i);
                    tagMapper.register(tagDTO);
                    Integer tagId = tagDTO.getId();
                    // M:N 관계 테이블 생성
                    tagMapper.createPostTag(tagId, postId);
                }
            } catch (RuntimeException e) {
                log.error("register ERROR! {}", postDTO);
                throw new RuntimeException("register ERROR! 게시글 등록 메서드를 확인해주세요" + postDTO);
            }
        } else {
            log.error("register ERROR! {}", postDTO);
            throw new RuntimeException("register ERROR! 게시글 등록 메서드를 확인해주세요" + postDTO);
        }
    }

    @Override
    public List<PostDTO> getMyPosts(int accountId) {
        List<PostDTO> postDTOList = null;
        try {
            postDTOList = postMapper.selectMyPosts(accountId);
        } catch (RuntimeException e) {
            log.error("getMyPosts ERROR! {}", accountId);
            throw new RuntimeException("getMyPosts ERROR! 게시글 조회 메서드를 확인해주세요" + accountId);
        }
        return postDTOList;
    }

    @Override
    public void updatePosts(PostDTO postDTO) {
        if (postDTO != null && postDTO.getId() != 0 && postDTO.getUserId() != 0) {
            try {
                postMapper.updatePosts(postDTO);
            } catch (RuntimeException e) {
                log.error("updatePosts ERROR! {}", postDTO);
                throw new RuntimeException("updatePosts ERROR! 게시글 수정 메서드를 확인해주세요" + postDTO);
            }
        } else {
            log.error("updatePosts ERROR! {}", postDTO);
            throw new RuntimeException("updatePosts ERROR! 게시글 수정 메서드를 확인해주세요" + postDTO);
        }
    }

    @Override
    public void deletePosts(int userId, int postId) {
        if (userId != 0 && postId != 0) {
            try {
                postMapper.deletePosts(postId);
            } catch (RuntimeException e) {
                log.error("deletePosts ERROR! {}", postId);
                throw new RuntimeException("deletePosts ERROR! 게시글 삭제 메서드를 확인해주세요" + postId);
            }
        } else {
            log.error("deletePosts ERROR! {}", postId);
            throw new RuntimeException("deletePosts ERROR! 게시글 삭제 메서드를 확인해주세요" + postId);
        }
    }

    @Override
    public void registerComment(CommentDTO commentDTO) {
        if (commentDTO.getPostId() != 0) {
            try {
                commentMapper.register(commentDTO);
            } catch (RuntimeException e) {
                log.error("register 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }

        } else {
            log.error("registerComment ERROR! {}", commentDTO);
            throw new RuntimeException("registerComment" + commentDTO);
        }
    }

    @Override
    public void updateComment(CommentDTO commentDTO) {
        if (commentDTO != null) {
            try {
                commentMapper.updateComments(commentDTO);
            } catch (RuntimeException e) {
                log.error("updateComments 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            log.error("updateComment ERROR!");
            throw new RuntimeException("updateComment");
        }
    }

    @Override
    public void deletePostComment(int userId, int commentId) {
        if (userId != 0 && commentId != 0) {
            try {
                commentMapper.deletePostComment(commentId);
            } catch (RuntimeException e) {
                log.error("deletePostComment 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            log.error("deletePostComment ERROR! {}", commentId);
            throw new RuntimeException("deletePostComment" + commentId);
        }
    }

    @Override
    public void registerTag(TagDTO tagDTO) {
        if (tagDTO.getPostId() != 0) {
            try {
                tagMapper.register(tagDTO);
            } catch (RuntimeException e) {
                log.error("register 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            log.error("registerTag ERROR! {}", tagDTO);
            throw new RuntimeException("registerTag ERROR! 태그 추가 메서드를 확인해주세요\n" + "Params : " + tagDTO);
        }
    }

    @Override
    public void updateTag(TagDTO tagDTO) {
        if (tagDTO != null) {
            try {
                tagMapper.updateTags(tagDTO);
            } catch (RuntimeException e) {
                log.error("updateTags 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }

        } else {
            log.error("updateTag ERROR!");
            throw new RuntimeException("updateTag ERROR!");
        }
    }

    @Override
    public void deletePostTag(int userId, int tagId) {
        if (userId != 0 && tagId != 0) {
            try {
                tagMapper.deletePostTag(tagId);
            } catch (RuntimeException e) {
                log.error("deletePostTag 실패");
                throw new BoardServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }

        } else {
            log.error("deletePostTag ERROR!");
            throw new RuntimeException("deletePostTag ERROR!");
        }
    }
}
