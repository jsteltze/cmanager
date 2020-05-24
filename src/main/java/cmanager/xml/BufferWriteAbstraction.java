package cmanager.xml;

import java.io.BufferedWriter;
import java.io.IOException;

abstract class BufferWriteAbstraction {

    public abstract BufferWriteAbstraction append(String string) throws IOException;

    public abstract String toString();

    public BufferWriteAbstraction append(BufferWriteAbstraction bufferWriteAbstraction)
            throws IOException {
        return append(bufferWriteAbstraction.toString());
    }

    public static class StringBufferWriteAbstraction extends BufferWriteAbstraction {

        private StringBuilder stringBuilder = null;

        @SuppressWarnings("unused")
        private StringBufferWriteAbstraction() {}

        public StringBufferWriteAbstraction(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
        }

        @Override
        public BufferWriteAbstraction append(String string) {
            stringBuilder.append(string);
            return this;
        }

        public String toString() {
            return stringBuilder.toString();
        }
    }

    public static class BufferedWriterWriteAbstraction extends BufferWriteAbstraction {

        private BufferedWriter bufferedWriter = null;

        @SuppressWarnings("unused")
        private BufferedWriterWriteAbstraction() {}

        public BufferedWriterWriteAbstraction(BufferedWriter bufferedWriter) {
            this.bufferedWriter = bufferedWriter;
        }

        @Override
        public BufferWriteAbstraction append(String string) throws IOException {
            bufferedWriter.write(string);
            return this;
        }

        @Override
        public String toString() {
            throw new IllegalAccessError();
        }

        public BufferedWriter getBW() {
            return bufferedWriter;
        }
    }
}
