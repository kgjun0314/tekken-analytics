package io.github.kgjun0314.tekken_analytics.player.controller;

import io.github.kgjun0314.tekken_analytics.common.dto.PageResponse;
import io.github.kgjun0314.tekken_analytics.common.exception.ErrorResponse;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchResponse;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerSummaryResponse;
import io.github.kgjun0314.tekken_analytics.player.query.PlayerQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
@Tag(
        name = "Player",
        description = "플레이어 조회 API"
)
public class PlayerController {
    private final PlayerQueryService queryService;

    @Operation(
            summary = "플레이어 요약 정보 조회",
            description = "플레이어의 전적, 승률 등의 요약 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "플레이어를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{userId}")
    public PlayerSummaryResponse getSummary(
            @PathVariable Long userId
    ) {
        return queryService.getSummary(userId);
    }

    @Operation(
            summary = "최근 경기 조회",
            description = "플레이어의 최근 경기 목록을 페이지 단위로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "플레이어를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{userId}/matches")
    public PageResponse<PlayerMatchResponse> getMatches(
            @PathVariable Long userId,
            @ParameterObject Pageable pageable
    ) {
        return queryService.getMatches(
                userId,
                pageable
        );
    }
}