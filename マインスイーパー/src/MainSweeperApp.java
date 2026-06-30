package src;

import javax.swing.SwingUtilities;

/**
 * マインスイーパーアプリケーションのエントリーポイント。
 * プログラムの起動処理を行う。
 */
public class MainSweeperApp {

    /**
     * メインメソッド
     * Swingのイベントディスパッチスレッドで
     * GameControllerを起動する
     *
     * @param args コマンドライン引数（未使用）
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameController::new);
    }
}