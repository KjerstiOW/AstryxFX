package com.kjersti.astryx.common.logging;

import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiOutputStream extends OutputStream {
    private final List<OutputStream> streams;

    public MultiOutputStream(OutputStream... outputStreams) {
        streams = new ArrayList<>();

        streams.addAll(Arrays.asList(outputStreams));
    }

    public void addStream(OutputStream stream) {
        streams.add(stream);
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream out : streams) {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream out : streams) {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream out : streams) {
            out.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream out : streams) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream out : streams) {
            out.close();
        }
    }
}
