package src;

import javax.swing.*;

/**
 * ゲーム時間を管理するクラス
 */
public class TimerManager {

    /** Swingタイマー */
    private Timer swingTimer;

    /** 経過秒数 */
    private int elapsedSeconds;

    /** 時間表示用ラベル */
    private JLabel timeDisplayLabel;

    /**
     * コンストラクタ
     * @param timeDisplayLabel 表示ラベル
     */
    public TimerManager(JLabel timeDisplayLabel) {

        this.timeDisplayLabel = timeDisplayLabel;
        this.elapsedSeconds = 0;

        // 1秒ごとに更新
        swingTimer = new Timer(1000, event -> {
            elapsedSeconds++;
            this.timeDisplayLabel.setText("Time: " + elapsedSeconds);
        });
    }

    /**
     * タイマー開始
     */
    public void start() {
        if (!swingTimer.isRunning()) {
            swingTimer.start();
        }
    }

    /**
     * タイマー停止
     */
    public void stop() {
        swingTimer.stop();
    }

    /**
     * タイマーリセット（追加しておくと便利）
     */
    public void reset() {
        elapsedSeconds = 0;
        timeDisplayLabel.setText("Time: 0");
    }
}