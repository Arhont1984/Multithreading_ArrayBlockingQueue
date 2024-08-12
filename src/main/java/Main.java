import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static String[] texts = new String[100_000];
    static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {

        //Генерации строк
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

// Создание блокирующих очередей
        BlockingQueue queueA = new ArrayBlockingQueue(100);
        BlockingQueue queueB = new ArrayBlockingQueue(100);
        BlockingQueue queueC = new ArrayBlockingQueue(100);

// Создание потоков для обработки очередей
        Thread threadA = new Thread(new CharCounter(queueA, 'a'));
        Thread threadB = new Thread(new CharCounter(queueB, 'b'));
        Thread threadC = new Thread(new CharCounter(queueC, 'c'));

        threadA.start();
        threadB.start();
        threadC.start();

// Заполнение очередей строками
        for (String text : texts) {
// Выбор на какую очередь помещать строку
            try {
                queueA.put(text);
                queueB.put(text);
                queueC.put(text);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

// Завершение потоков
        threadA.interrupt();
        threadB.interrupt();
        threadC.interrupt();
// Ожидание завершения потоков
        threadA.join();
        threadB.join();
        threadC.join();
    }

    //Генератор текстов
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    static class CharCounter implements Runnable {
        private final BlockingQueue queue;
        private final char targetChar;

        public CharCounter(BlockingQueue queue, char targetChar) {
            this.queue = queue;
            this.targetChar = targetChar;
        }

        @Override
        public void run() {
            String maxText = "";
            int maxCount = 0;
            try {
                while (true) {
                    String text = (String) queue.take();
                    int count = countChar(text, targetChar);
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                }
            } catch (InterruptedException e) {
                // Поток завершается, когда его прерывают
                System.out.println("Поток " + targetChar + " завершился: максимальное количество символов '"
                        + targetChar + "' в строке: \"" + maxText + "\" (" + maxCount + " раз).");
            }
        }

        private int countChar(String text, char targetChar) {
            int count = 0;
            for (char c : text.toCharArray()) {
                if (c == targetChar) {
                    count++;
                }
            }
            return count;
        }
    }
}





