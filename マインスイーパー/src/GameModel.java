package src;

import java.util.Random; // ランダムな数値を生成するためのクラスをインポート

/**
 * ゲームの状態とロジックを管理するクラス。
 * 地雷配置・セル展開・クリア判定などを行う。
 */
public class GameModel {

    /** 盤面の行数 */
    private int totalRows;

    /** 盤面の列数 */
    private int totalColumns;

    /** 地雷の総数 */
    private int totalMineCount;

    /** セルの2次元配列（盤面） */
    private Cell[][] cellGrid;

    /** 初期化済みかどうか（地雷配置済みか） */
    private boolean isInitialized = false;

    /**
     * コンストラクタ
     */
    public GameModel(int totalRows, int totalColumns, int totalMineCount) {

        this.totalRows = totalRows;           // 行数を設定
        this.totalColumns = totalColumns;     // 列数を設定
        this.totalMineCount = totalMineCount; // 地雷数を設定

        // セル配列を生成（まだ中身は空）
        cellGrid = new Cell[totalRows][totalColumns];

        // 空のセルをすべて初期化
        initializeEmptyBoard();
    }

    /**
     * 空の盤面を生成
     */
    private void initializeEmptyBoard() {

        // 行方向ループ
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {

            // 列方向ループ
            for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {

                // 各セルに新しいCellインスタンスを代入
                cellGrid[rowIndex][columnIndex] = new Cell();
            }
        }
    }

    /**
     * 地雷をランダムに配置する
     */
    public void initializeMines(int safeRowIndex, int safeColumnIndex) {

        // すでに配置済みなら何もしない
        if (isInitialized) return;

        Random random = new Random(); // 乱数生成オブジェクト

        int placedMineCount = 0; // 配置済みの地雷数

        // 地雷数が目標に達するまで繰り返す
        while (placedMineCount < totalMineCount) {

            // ランダムな行と列を取得
            int randomRowIndex = random.nextInt(totalRows);
            int randomColumnIndex = random.nextInt(totalColumns);

            //  初手クリック周囲は安全にする
            if (Math.abs(randomRowIndex - safeRowIndex) <= 1 &&
                Math.abs(randomColumnIndex - safeColumnIndex) <= 1) {
                continue; // この位置はスキップ
            }

            // すでに地雷がある場合はスキップ
            if (cellGrid[randomRowIndex][randomColumnIndex].hasMine()) {
                continue;
            }

            // 地雷をセット
            cellGrid[randomRowIndex][randomColumnIndex].setMine(true);

            // 地雷数をカウント
            placedMineCount++;
        }

        // 周囲地雷数を計算
        calculateAdjacentMineCounts();

        // 初期化完了フラグON
        isInitialized = true;
    }

    /**
     * 全セルの周囲地雷数を計算
     */
    private void calculateAdjacentMineCounts() {

        // 全セルをループ
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {

                // 各セルの周囲地雷数を計算
                int adjacentMineCount =
                        countNeighborMines(rowIndex, columnIndex);

                // セルにセット
                cellGrid[rowIndex][columnIndex]
                        .setAdjacentMineCount(adjacentMineCount);
            }
        }
    }

    /**
     * 周囲の地雷数を数える
     */
    private int countNeighborMines(int baseRowIndex, int baseColumnIndex) {

        int mineCount = 0; // 地雷カウント

        // 周囲8マスを探索
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                // 隣接セルの位置
                int neighborRowIndex = baseRowIndex + rowOffset;
                int neighborColumnIndex = baseColumnIndex + columnOffset;

                // 範囲外ならスキップ
                if (neighborRowIndex < 0 || neighborColumnIndex < 0 ||
                    neighborRowIndex >= totalRows ||
                    neighborColumnIndex >= totalColumns) {
                    continue;
                }

                // 地雷ならカウント
                if (cellGrid[neighborRowIndex][neighborColumnIndex]
                        .hasMine()) {
                    mineCount++;
                }
            }
        }

        return mineCount; // 地雷数を返す
    }

    /**
     * セルを開く（再帰展開）
     */
    public void revealCell(int targetRowIndex, int targetColumnIndex) {

        // 範囲外チェック
        if (targetRowIndex < 0 || targetColumnIndex < 0 ||
            targetRowIndex >= totalRows || targetColumnIndex >= totalColumns) {
            return;
        }

        // 対象セル取得
        Cell targetCell =
                cellGrid[targetRowIndex][targetColumnIndex];

        // すでに開いている or フラグなら処理しない
        if (targetCell.isRevealed() || targetCell.hasFlag()) return;

        // セルを開く
        targetCell.reveal();

        // 周囲地雷0かつ地雷でない場合
        if (!targetCell.hasMine() &&
            targetCell.getAdjacentMineCount() == 0) {

            // 8方向を再帰的に開く
            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                    // 自分以外なら再帰
                    if (rowOffset != 0 || columnOffset != 0) {

                        revealCell(
                                targetRowIndex + rowOffset,
                                targetColumnIndex + columnOffset
                        );
                    }
                }
            }
        }
    }

    /** セル取得 */
    public Cell getCell(int rowIndex, int columnIndex) {
        return cellGrid[rowIndex][columnIndex]; // 指定位置のセル返す
    }

    /** クリア判定 */
    public boolean isCleared() {

        // 全セルをチェック
        for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {

                Cell currentCell = cellGrid[rowIndex][columnIndex];

                // 地雷以外で未開封があれば未クリア
                if (!currentCell.hasMine() &&
                    !currentCell.isRevealed()) {
                    return false;
                }
            }
        }

        // 全て開かれていればクリア
        return true;
    }

    /** 行数取得 */
    public int getTotalRows() { return totalRows; }

    /** 列数取得 */
    public int getTotalColumns() { return totalColumns; }

    /** 地雷数取得 */
    public int getTotalMineCount() { return totalMineCount; }
}