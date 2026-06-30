package src;
/**
 * マインスイーパーの1マス（セル）を表すクラス。
 * 各セルは地雷の有無、開かれているか、フラグの有無などの状態を持つ。
 */
public class Cell {

    /** このセルに地雷が存在するか */
    private boolean hasMine;

    /** このセルが既に開かれているか */
    private boolean isRevealed;

    /** このセルにフラグが立てられているか */
    private boolean hasFlag;

    /** 周囲8マスに存在する地雷の数 */
    private int adjacentMineCount;

    /**
     * 地雷があるかを取得する
     * @return 地雷が存在する場合はtrue
     */
    public boolean hasMine() {
        return hasMine;
    }

    /**
     * 地雷の有無を設定する
     * @param hasMine trueの場合、このセルを地雷にする
     */
    public void setMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    /**
     * セルが開かれているかを取得する
     * @return 開かれている場合はtrue
     */
    public boolean isRevealed() {
        return isRevealed;
    }

    /**
     * セルを開く
     * 開封された状態に変更する
     */
    public void reveal() {
        this.isRevealed = true;
    }

    /**
     * フラグが立っているかを取得
     * @return フラグがある場合はtrue
     */
    public boolean hasFlag() {
        return hasFlag;
    }

    /**
     * フラグのON/OFFを切り替える
     */
    public void toggleFlag() {
        this.hasFlag = !this.hasFlag;
    }

    /**
     * 周囲の地雷数を取得
     * @return 周囲の地雷数
     */
    public int getAdjacentMineCount() {
        return adjacentMineCount;
    }

    /**
     * 周囲の地雷数を設定
     * @param adjacentMineCount 地雷数
     */
    public void setAdjacentMineCount(int adjacentMineCount) {
        this.adjacentMineCount = adjacentMineCount;
    }
}