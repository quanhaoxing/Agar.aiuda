package Audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioServidor2 extends Thread {
	public final static int TIME_SLEEP = 300;
	public final static int AUDIO_PORT = 9786;
	public final static int FORMAT_PORT = 9787;
	public final static String IP_DATOS = "239.1.2.2";
	
	private byte audioBuffer[] = new byte[60000];
	private byte formatBuffer[] = new byte[60000];
	private TargetDataLine targetDataLine;
	private AudioInputStream audioStream;
	private DatagramSocket socketMusica ;
//	private MulticastSocket multiSocketMusic;
//	private DatagramSocket socketFormato ;
	private File file;
	private String[]Canciones;
	InetAddress inetAddress ;
	
	@Override
	public void run() {
		broadcastAudio();
	}

	public AudioServidor2(String song) {
		try {
			Canciones = new String[4];
			Canciones[0] = "Legends Never Die";
			Canciones[1] = "pumped";
			Canciones[2] = "RISE";
			Canciones[3] = "Yoshi";
			System.out.println("preparando formato");
			socketMusica = new DatagramSocket();
//			multiSocketMusic = new MulticastSocket();
//			socketFormato = new DatagramSocket();
			file= new File("./Musica/"+song.trim()+".wav");
			audioStream= AudioSystem.getAudioInputStream(file);
			inetAddress = InetAddress.getByName(IP_DATOS);
//			multiSocketMusic.joinGroup(inetAddress);
			System.out.println("formato preparado");
			setupAudio();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcastAudio() {
		try {		
			System.out.println("preparandose para sonar");
			while (true) {
				System.out.println("sonando");
				int count = audioStream.read(audioBuffer, 0, audioBuffer.length);
				if (count > 0) {
					
					String infoFormat = audioStream.getFormat().getSampleRate()+" "+audioStream.getFormat().getSampleSizeInBits()+" "+audioStream.getFormat().getChannels();
					formatBuffer = infoFormat.getBytes();
					
					System.out.println("preparando formato");
					DatagramPacket packetFormat =  new DatagramPacket(formatBuffer, formatBuffer.length, inetAddress, FORMAT_PORT);
					socketMusica.send(packetFormat);
					System.out.println("formato enviado");
					
					System.out.println("preparando cancion");
					DatagramPacket packet = new DatagramPacket(audioBuffer, audioBuffer.length, inetAddress, AUDIO_PORT);
					socketMusica.send(packet);
					System.out.println("cancion enviada");
					sleep(TIME_SLEEP);
				}
			}
		} catch (Exception ex) {
			 System.out.println(ex);
		}
	}

	public void setupAudio() {
		try {
			System.out.println("preparando setup");
			AudioFormat audioFormat =audioStream.getFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			System.out.println("setup preparado");
			start();
		} catch (Exception ex) {
			 System.out.println(ex);
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public String[] getCanciones() {
		return Canciones;
	}

	public void setCanciones(String[] canciones) {
		Canciones = canciones;
	}

	public static void main(String[] args) {
		AudioServidor2 as = new AudioServidor2("pumped");
	}
	
}
