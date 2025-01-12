package com.github.cichyvx.openmath.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "openmath")
public class OpenMathConfig {

    private Long endGameClosingTaskRate;
    private Long playerMatchmakingTaskRate;
    private Long gameStartingTaskRate;
    private Long sendMessageToClientTaskRate;

    private int maxSendRetry;
    private Long retryMessageDelay;

    private Long waitingRoomTime;

    public Long getEndGameClosingTaskRate() {
        return endGameClosingTaskRate;
    }

    public void setEndGameClosingTaskRate(Long endGameClosingTaskRate) {
        this.endGameClosingTaskRate = endGameClosingTaskRate;
    }

    public Long getPlayerMatchmakingTaskRate() {
        return playerMatchmakingTaskRate;
    }

    public void setPlayerMatchmakingTaskRate(Long playerMatchmakingTaskRate) {
        this.playerMatchmakingTaskRate = playerMatchmakingTaskRate;
    }

    public Long getGameStartingTaskRate() {
        return gameStartingTaskRate;
    }

    public void setGameStartingTaskRate(Long gameStartingTaskRate) {
        this.gameStartingTaskRate = gameStartingTaskRate;
    }

    public Long getSendMessageToClientTaskRate() {
        return sendMessageToClientTaskRate;
    }

    public void setSendMessageToClientTaskRate(Long sendMessageToClientTaskRate) {
        this.sendMessageToClientTaskRate = sendMessageToClientTaskRate;
    }

    public int getMaxSendRetry() {
        return maxSendRetry;
    }

    public void setMaxSendRetry(int maxSendRetry) {
        this.maxSendRetry = maxSendRetry;
    }

    public Long getRetryMessageDelay() {
        return retryMessageDelay;
    }

    public void setRetryMessageDelay(Long retryMessageDelay) {
        this.retryMessageDelay = retryMessageDelay;
    }

    public Long getWaitingRoomTime() {
        return waitingRoomTime;
    }

    public void setWaitingRoomTime(Long waitingRoomTime) {
        this.waitingRoomTime = waitingRoomTime;
    }
}
