package alarm;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WinningMessage implements Runnable {
    @Override
    public void run() {
        String path = "C:\\Users\\egoro\\IdeaProjects\\LichessPlayer\\src\\sounds\\razval.wav";

        try {
            File soundFile = new File(path); //Звуковой файл

            //Получаем AudioInputStream
            //Вот тут могут полететь IOException и UnsupportedAudioFileException
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);

            //Получаем реализацию интерфейса Clip
            //Может выкинуть LineUnavailableException
            Clip clip = AudioSystem.getClip();

            //Загружаем наш звуковой поток в Clip
            //Может выкинуть IOException и LineUnavailableException
            clip.open(ais);

            clip.setFramePosition(0); //устанавливаем указатель на старт
            clip.start(); //Поехали!!!

            //Если не запущено других потоков, то стоит подождать, пока клип не закончится
            //В GUI-приложениях следующие 3 строчки не понадобятся
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.stop(); //Останавливаем
            clip.close(); //Закрываем
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException exc) {
            System.err.println(exc.getLocalizedMessage());
            exc.printStackTrace();
        }
    }
}
