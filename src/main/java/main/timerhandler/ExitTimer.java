package main.timerhandler;

import main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ExitTimer implements Runnable {
    private static final Map<Player, Integer> exitTimer = new HashMap<>();
    @Override
    public void run() {
        if (!exitTimer.isEmpty()) {
            for (Map.Entry<Player, Integer> entry : exitTimer.entrySet()) {
                exitTimer.put(entry.getKey(), entry.getValue() - 1);
                if (entry.getValue() <= 0) {
                    exitTimer.remove(entry.getKey());
                    entry.getKey().kickPlayer(Main.INDEX + "현재 로비로 돌아가는 코드가 없으므로 서버에서 나가졌습니다.");
                } else if (entry.getValue() == 40 || entry.getValue() == 20) entry.getKey().sendMessage(Main.INDEX + "§e" + entry.getValue()/20 + "초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
            }
        }
    } public static Map<Player, Integer> getExitTimer() {
        return exitTimer;
    }
}
