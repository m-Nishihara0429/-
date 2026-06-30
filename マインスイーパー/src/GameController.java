
package src;

import javax.swing.*;                         // Swingライブラリ（UI用）
import java.awt.event.MouseAdapter;           // マウスイベント処理クラス
import java.awt.event.MouseEvent;

/**
 * ゲームの制御を行うクラス（Controller）
 * ユーザー操作の受付と、Model・Viewの制御を担当する
 */
public class GameController {

    private GameModel gameModel;              // ゲームロジック（Model）
    private GameView gameView;                // 画面表示（View）
    private TimerManager timerManager;        // タイマー管理クラス

    /** ゲームが開始されているかどうか */
    private boolean isGameStarted = false;

    /** ゲームが終了しているかどうか */
    private boolean isGameOver = false;

    /** 現在置かれているフラグの数 */
    private int currentFlagCount = 0;

    /**
     * コンストラクタ
     * 初期設定（難易度選択・画面生成など）を行う
     */
    public GameController() {

        // 難易度選択用の配列
        String[] difficultyOptions = {"Easy", "Normal", "Hard"};

        // ダイアログを表示してユーザーに難易度を選ばせる
        int selectedDifficulty = JOptionPane.showOptionDialog(
                null,                              // 親コンポーネントなし
                "難易度を選択してください",        // メッセージ
                "Difficulty",                     // タイトル
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                difficultyOptions,
                difficultyOptions[0]
        );

        int totalRows;        // 行数
        int totalColumns;     // 列数
        int totalMineCount;   // 地雷数

        // 難易度によって盤面設定を決定
        switch (selectedDifficulty) {
            case 1: // Normal
                totalRows = 16;
                totalColumns = 16;
                totalMineCount = 40;
                break;
            case 2: // Hard
                totalRows = 16;
                totalColumns = 30;
                totalMineCount = 99;
                break;
            default: // Easy
                totalRows = 9;
                totalColumns = 9;
                totalMineCount = 10;
        }

        // Model（ロジック）を生成
        gameModel = new GameModel(totalRows, totalColumns, totalMineCount);

        // View（画面）を生成
        gameView = new GameView(totalRows, totalColumns, totalMineCount);

        // タイマー初期化（右上ラベルと紐づけ）
        timerManager = new TimerManager(gameView.timeLabel);

        // フラグ数を初期化
        currentFlagCount = 0;

        // 地雷カウンタ表示を更新
        updateMineCounter();

        // セルクリックイベント設定
        setUpEvents();

        // 画面を表示
        gameView.setVisible(true);
    }

    /**
     * 各セルにクリックイベントを設定する
     */
    private void setUpEvents() {

        // 全セルをループ
        for (int rowIndex = 0; rowIndex < gameModel.getTotalRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gameModel.getTotalColumns(); columnIndex++) {

                int clickedRowIndex = rowIndex;       // 行を固定（内部クラス用）
                int clickedColumnIndex = columnIndex; // 列を固定

                // 各セルにマウスイベントを追加
                gameView.cellButtons[clickedRowIndex][clickedColumnIndex]
                        .addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {

                        // ゲーム終了後は操作できない
                        if (isGameOver) return;

                        // ===== 左クリック処理 =====
                        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {

                            // 初回クリック時のみ地雷配置＆タイマー開始
                            if (!isGameStarted) {

                                // 初手安全で地雷配置
                                gameModel.initializeMines(
                                        clickedRowIndex,
                                        clickedColumnIndex
                                );

                                // タイマー開始
                                timerManager.start();

                                // ゲーム開始フラグON
                                isGameStarted = true;
                            }

                            // クリックしたセル取得
                            Cell clickedCell =
                                    gameModel.getCell(clickedRowIndex, clickedColumnIndex);

                            // 地雷なら即ゲームオーバー
                            if (clickedCell.hasMine()) {

                                clickedCell.reveal();  // 地雷を表示
                                handleGameOver();      // 終了処理
                                return;
                            }

                            // セルを開く（連鎖展開あり）
                            gameModel.revealCell(clickedRowIndex, clickedColumnIndex);

                            // 画面更新
                            refreshView();
                        }

                        // ===== 右クリック処理（フラグ） =====
                        if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                            // 対象セル取得
                            Cell targetCell =
                                    gameModel.getCell(clickedRowIndex, clickedColumnIndex);

                            // 開いているセルにはフラグ不可
                            if (targetCell.isRevealed()) return;

                            // フラグ変更前の状態
                            boolean wasFlagged = targetCell.hasFlag();

                            // フラグON/OFF切り替え
                            targetCell.toggleFlag();

                            // フラグ数を更新
                            if (!wasFlagged && targetCell.hasFlag()) {
                                currentFlagCount++;  // 新しく置いた
                            } else if (wasFlagged && !targetCell.hasFlag()) {
                                currentFlagCount--;  // 外した
                            }

                            // 残り地雷数表示を更新
                            updateMineCounter();

                            // 画面更新
                            refreshView();
                        }
                    }
                });
            }
        }

        // ===== リセットボタン処理 =====
        gameView.getResetButton().addActionListener(event -> {

            timerManager.stop();   // タイマー停止
            timerManager.reset();  // タイマーリセット

            currentFlagCount = 0;  // フラグ数リセット

            gameView.dispose();    // 画面を閉じる
            new GameController();  // 新しいゲーム開始
        });
    }

    /**
     * 盤面全体を更新する
     */
    private void refreshView() {

        // 全セル更新
        for (int rowIndex = 0; rowIndex < gameModel.getTotalRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gameModel.getTotalColumns(); columnIndex++) {

                gameView.updateCell(
                        rowIndex,
                        columnIndex,
                        gameModel.getCell(rowIndex, columnIndex)
                );
            }
        }

        // ゲーム終了なら判定しない
        if (isGameOver) return;

        // クリア判定
        if (gameModel.isCleared()) {
            timerManager.stop();  // タイマー停止
            isGameOver = true;
            JOptionPane.showMessageDialog(gameView, "クリア！");
        }
    }

    /**
     * ゲームオーバー処理
     */
    private void handleGameOver() {

        isGameOver = true;     // 終了フラグ
        timerManager.stop();   // タイマー停止

        // 全セルを開く
        for (int rowIndex = 0; rowIndex < gameModel.getTotalRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < gameModel.getTotalColumns(); columnIndex++) {

                gameModel.getCell(rowIndex, columnIndex).reveal();
            }
        }

        refreshView();  // 表示更新

        // メッセージ表示
        JOptionPane.showMessageDialog(gameView, "ゲームオーバー！");
    }

    /**
     * 残り地雷数表示更新
     */
    private void updateMineCounter() {

        // 残り地雷数 = 総地雷数 - フラグ数
        int remainingMineCount =
                gameModel.getTotalMineCount() - currentFlagCount;

        // ラベルに表示
        gameView.mineCountLabel.setText("Mines: " + remainingMineCount);
    }
}