package me.flashyreese.mods.ping.client.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLUUtils {
    public static boolean gluProject(float objectX, float objectY, float objectZ, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer winPos) {
        float[] in = new float[4];
        float[] out = new float[4];

        in[0] = objectX;
        in[1] = objectY;
        in[2] = objectZ;
        in[3] = 1.0f;

        gluMultMatrixVecf(modelMatrix, in, out);
        gluMultMatrixVecf(projMatrix, out, in);

        if (in[3] == 0.0) {
            return false;
        }

        in[3] = (1.0f / in[3]) * 0.5f;

        // Map x, y and z to range 0-1
        in[0] = in[0] * in[3] + 0.5f;
        in[1] = in[1] * in[3] + 0.5f;
        in[2] = in[2] * in[3] + 0.5f;

        // Map x,y to viewport
        winPos.put(0, in[0] * viewport.get(viewport.position() + 2) + viewport.get(viewport.position()));
        winPos.put(1, in[1] * viewport.get(viewport.position() + 3) + viewport.get(viewport.position() + 1));
        winPos.put(2, in[2]);
        return true;
    }

    private static void gluMultMatrixVecf(FloatBuffer m, float[] in, float[] out) {
        for (int i = 0; i < 4; i++) {
            out[i] = in[0] * m.get(m.position() + i)
                    + in[1] * m.get(m.position() + 4 + i)
                    + in[2] * m.get(m.position() + 2 * 4 + i)
                    + in[3] * m.get(m.position() + 3 * 4 + i);

        }
    }
}