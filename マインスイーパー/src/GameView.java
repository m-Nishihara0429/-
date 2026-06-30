package src;

import javax.swing.*;      // Swing UIコンポーネント
import java.awt.*;         // レイアウト・画面サイズなど

/**
 * ユーザーインターフェース（画面表示）を管理するクラス（View）
 * セルボタンやラベルの表示・更新を担当する。
 */
public class GameView extends JFrame {

    /** セルのボタン一覧（盤面の各マス） */
    JButton[][] cellButtons;

    /** 経過時間を表示するラベル */
    JLabel timeLabel;

    /** 残り地雷数を表示するラベル */
    JLabel mineCountLabel;

    /** リセットボタン */
    JButton resetButton;

    /**
     * コンストラクタ
     * 画面の初期レイアウトとUIを生成する
     */
    public GameView(int totalRows, int totalColumns, int totalMineCount) {

        // 画面サイズを取得（PC画面全体）
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = screenSize.width;    // 横幅
        int height = screenSize.height;  // 高さ

        // ウィンドウタイトル設定
        setTitle("Minesweeper");

        // ウィンドウサイズ設定（下のバー分少し引く）
        setSize(width, height - 50);

        // 閉じるボタンでアプリ終了
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 全体レイアウトをBorderLayoutに設定（上・中央・下など）
        setLayout(new BorderLayout());

        // ===== 上部パネル（時間・リセット・地雷数） =====
        JPanel topPanel = new JPanel(new GridLayout(1, 3)); // 横3分割

        // タイマー表示ラベル（中央揃え）
        timeLabel = new JLabel("Time: 0", SwingConstants.CENTER);

        // 地雷数ラベル（中央揃え）
        mineCountLabel = new JLabel(
                "Mines: " + totalMineCount,
                SwingConstants.CENTER
        );

        // リセットボタン作成
        resetButton = new JButton("Reset");

        // パネルに追加（左→中央→右）
        topPanel.add(timeLabel);
        topPanel.add(resetButton);
        topPanel.add(mineCountLabel);

        // 上部に設置
        add(topPanel, BorderLayout.NORTH);

        // ===== 相部パネル（セルのグリッド） =====
        JPanel gridPanel = new JPanel(
                new GridLayout(totalRows, totalColumns)
        );

        // セルボタン配列を生成
        cellButtons = new JButton[totalRows][totalColumns];

        // 全セルをループ
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {

                // 各セルにボタンを生成
                cellButtons[rowIndex][columnIndex] = new JButton();

                // グリッドに追加
                gridPanel.add(cellButtons[rowIndex][columnIndex]);
            }
        }

        // 画面中央にグリッドを配置
        add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * セルの状態に応じてボタン表示を更新する
     */
    public void updateCell(int rowIndex, int columnIndex, Cell cell) {

        // 対象ボタン取得
        JButton targetButton = cellButtons[rowIndex][columnIndex];

        // ===== セルが開かれている場合 =====
        if (cell.isRevealed()) {

            targetButton.setEnabled(false); // 押せなくする

            // 地雷の場合
            if (cell.hasMine()) {

                targetButton.setText("💣"); // 地雷表示

            // 地雷が周囲にある場合
            } else if (cell.getAdjacentMineCount() > 0) {

                // 地雷数を表示
                targetButton.setText(
                        String.valueOf(cell.getAdjacentMineCount())
                );
            }

        // ===== フラグがある場合 =====
        } else if (cell.hasFlag()) {

            targetButton.setText("🚩"); // フラグ表示

        // ===== それ以外 =====
        } else {

            targetButton.setText(""); // 空表示
        }
    }

    /**
     * リセットボタンを取得する
     */
    public JButton getResetButton() {
        return resetButton; // Controllerから使うため
    }
}