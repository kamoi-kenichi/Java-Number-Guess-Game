import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Random random = new Random();

    static Map<String, Integer> bestScores = new HashMap<>();

    static {
        bestScores.put("かんたん", null);
        bestScores.put("ふつう", null);
        bestScores.put("むずかしい", null);
    }

    public static void main(String[] args) {

        loadData();

        do {
            startGame();
        } while (askReplay());

        saveData();

        System.out.println("ゲームを終了します。");
        scanner.close();
    }

    static void startGame() {
        playCount++;

        int difficulty = selectDifficulty();
        int max = getMaxByDifficulty(difficulty);
        int answer = random.nextInt(max) + 1;
        int count = 0;
        int guess;

        System.out.println("=== 数当てゲーム ===");
        System.out.println("1～" + max + " の数字を当ててください。");

        do {
            guess = getUserInput(max);
            count++;
            judge(guess, answer);
        } while (guess != answer);

        System.out.println(count + "回で当たりました！");
        showScore(count, difficulty);
        saveData();
        System.out.println();
    }

    static int selectDifficulty() {
        while (true) {
            System.out.println("難易度を選んでください：");
            System.out.println("1: かんたん（1～10）");
            System.out.println("2: ふつう（1～50）");
            System.out.println("3: むずかしい（1～100）");
            System.out.print("番号を入力してください：");

            String choice = scanner.next();

            switch (choice) {
                case "1":
                    return 1;
                case "2":
                    return 2;
                case "3":
                    return 3;
                default:
                    System.out.println("⚠ 1〜3を入力してください。");
            }
        }
    }

    static int getMaxByDifficulty(int difficulty) {
        switch (difficulty) {
            case 1:
                return 10;
            case 2:
                return 50;
            case 3:
                return 100;
            default:
                return 100;
        }
    }

    static String getDifficultyName(int difficulty) {
        switch (difficulty) {
            case 1:
                return "かんたん";
            case 2:
                return "ふつう";
            case 3:
                return "むずかしい";
            default:
                return "不明";
        }
    }

    static int playCount = 0;
    static String lastPlayed = "未プレイ";
    static final String SAVE_FILE = "game_data.txt";

    static void loadData() {
        try (Scanner fileScanner = new Scanner(new java.io.File(SAVE_FILE))) {

            playCount = Integer.parseInt(fileScanner.nextLine());
            lastPlayed = fileScanner.nextLine();

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length == 2) {
                    try {
                        int score = Integer.parseInt(parts[1]);
                        bestScores.put(parts[0], score);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠ データ破損スキップ: " + line);
                    }
                } else {
                    System.out.println("⚠ 不正データ: " + line);
                }
            }

        } catch (Exception e) {
            System.out.println("初回起動です！");
        }
    }

    static void saveData() {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(SAVE_FILE)) {

            pw.println(playCount);
            pw.println(lastPlayed);

            for (String key : bestScores.keySet()) {
                pw.println(key + "," + bestScores.get(key));
            }

        } catch (Exception e) {
            System.out.println("保存エラー");
        }
    }

    static int getUserInput(int max) {
        int input;

        while (true) {
            System.out.print("1～" + max + " の数字を入力してください： ");

            if (scanner.hasNextInt()) {
                input = scanner.nextInt();

                if (input >= 1 && input <= max) {
                    return input;
                } else {
                    System.out.println("⚠ 範囲外です！");
                }
            } else {
                System.out.println("⚠ 数字を入力してください！");
                scanner.next();
            }
        }
    }

    static void judge(int guess, int answer) {
        int diff = Math.abs(guess - answer);

        if (guess < answer) {
            System.out.println("もっと大きいです。");
        } else if (guess > answer) {
            System.out.println("もっと小さいです。");
        } else {
            System.out.println("正解です！");

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy/MM/dd HH:mm");

            lastPlayed = java.time.LocalDateTime.now().format(formatter);

            System.out.println("=== プレイ情報 ===");
            System.out.println("プレイ回数：" + playCount);
            System.out.println("最終プレイ：" + lastPlayed);
            return;
        }

        if (diff <= 5) {
            System.out.println("🔥かなり近いです！");
        } else if (diff <= 20) {
            System.out.println("🙂まあまあ近いです。");
        } else {
            System.out.println("❄まだ遠いです。");
        }
    }

    static void showScore(int count, int difficulty) {
        String difficultyName = getDifficultyName(difficulty);

        System.out.println("今回の回数：" + count + "回");
        System.out.println("難易度：" + difficultyName);

        Integer best = bestScores.get(difficultyName);

        if (best == null || count < best) {
            bestScores.put(difficultyName, count);
            System.out.println("🎉" + difficultyName + " の最少回数を更新しました！");
        }

        System.out.println(difficultyName + " の最少回数：" + bestScores.get(difficultyName) + "回");
        System.out.println();
        showAllScores();
    }

    static void showAllScores() {
        System.out.println("=== スコア 一覧===");

        String[] difficulties = { "かんたん", "ふつう", "むずかしい" };

        for (String diff : difficulties) {
            Integer score = bestScores.get(diff);

            if (score != null) {
                System.out.println(diff + "：" + score + "回");
            } else {
                System.out.println(diff + "：未プレイ");
            }
        }
    }

    static boolean askReplay() {
        while (true) {
            System.out.println("もう一度遊びますか？(y/n)：");
            String answer = scanner.next();

            if (answer.equalsIgnoreCase("y")) {
                return true;
            } else if (answer.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println("⚠ y か n を入力してください。");
            }
        }
    }
}
