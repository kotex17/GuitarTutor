package guitartutorandanalyser.guitartutor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class SoundRecorder {

    final int SAMPLE_RATE = 44100;
    final String PATH_NAME = Environment.getExternalStorageDirectory() + "/GuitarTutorRec.wav";
    final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT) / 2;

    AudioRecord recorder;
    private boolean isSoundRecording;
    int recSizeInByte;

    public Thread startRecording() { // creates new audio recorder instance, start it, new recording thread

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        Thread recordAudioThread = new Thread(new Runnable() {

            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recorder.startRecording(); // start capturing samples from mic, but do nothing with them( not writing into file)
        isSoundRecording = true;

        return recordAudioThread;
    }

    private void writeAudioDataToFile() {
        // Read audio data into short buffer, Write the output audio in byte

        short sData[] = new short[BUFFER_SIZE];

        try {
            // first create valami file with dummy wav header

            FileOutputStream outStream = new FileOutputStream(PATH_NAME);
            outStream.write(createWavHeader());

            while (isSoundRecording) {
                // read audio from microphone, short format
                recorder.read(sData, 0, BUFFER_SIZE);

                // writes audio data into file from buffer
                byte bData[] = short2byte(sData);
                outStream.write(bData, 0, BUFFER_SIZE * 2);
                recSizeInByte += bData.length;
            }

            outStream.close();
        } catch (IOException e) {
            Log.d("Audio record error", e.getMessage().toString());
        }
    }

    private byte[] short2byte(short[] sData) {
        // all android supports little endianness, Android ARM systems are bi endian, by deafault littleendian (manualy can be swithced to big endian)
        //android audio format PCM16bit record in the default device native endian
        byte[] bytes = new byte[sData.length * 2];

        for (int i = 0; i < sData.length; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF); //hexadecimal 00 FF = 0000 0000 1111 1111 masking
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8); // shift 8 right,
        }

        return bytes;
    }

    private byte[] createWavHeader() {
        // The default byte ordering assumed for WAVE data files is little-endian. Files written using the big-endian byte ordering scheme have the identifier RIFX instead of RIFF.

        short channels = 1; //mono
        short bitsPerSample = 16; // pcm 16bit

        //   int subchunk2Size = (int) recordedAudioLength; // int enough, recorded audio is max valami few minutes
        //   int chunkSize = (int) (recordedAudioLength + 36); //wave file header is 44 bytes, first 4 is ChunkId, second 4 bytes are ChunkSize: 44 - 8 = 36 bytes

        byte[] headerBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(SAMPLE_RATE)
                .putInt(SAMPLE_RATE * channels * (bitsPerSample / 8))
                .putShort((short) (channels * (bitsPerSample / 8)))
                .putShort(bitsPerSample)
                .array();

        byte[] header = new byte[]{
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize
                'W', 'A', 'V', 'E', // Format
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // audioformat: 1=PCM
                headerBytes[0], headerBytes[1], // No. of channels 1= mono, 2 = stereo
                headerBytes[2], headerBytes[3], headerBytes[4], headerBytes[5], // SampleRate
                headerBytes[6], headerBytes[7], headerBytes[8], headerBytes[9], // ByteRate
                headerBytes[10], headerBytes[11], // BlockAlign
                headerBytes[12], headerBytes[13], // BitsPerSample
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0 // Subchunk2Size
        };

        recSizeInByte += header.length;

        return header;
    }

    private void updateWavHeader() {

        byte[] bytesToUpdate = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(recSizeInByte - 8) // ChunkSize
                .putInt(recSizeInByte - 44) // Subchunk2Size
                .array();

        try {
            RandomAccessFile randomAccesFile = new RandomAccessFile(PATH_NAME, "rw");

            randomAccesFile.seek(4);
            randomAccesFile.write(bytesToUpdate, 0, 4);

            randomAccesFile.seek(40);
            randomAccesFile.write(bytesToUpdate, 4, 4);

            randomAccesFile.close();

        } catch (IOException e) {

        }


    }

    public void stopRecording() {

        if (recorder != null && isSoundRecording) {


            recorder.release();
            recorder = null;
            isSoundRecording = false;
        }

        updateWavHeader();
    }

   /* public boolean isSoundRecording() {
        return isSoundRecording;
    }*/
}
