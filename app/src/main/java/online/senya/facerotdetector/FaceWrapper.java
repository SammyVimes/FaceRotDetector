package online.senya.facerotdetector;

import com.google.android.gms.vision.face.Face;

/**
 * Created by Semyon on 22.11.2016.
 */

public class FaceWrapper {

    private Face face;

    public void setFace(final Face face) {
        this.face = face;
    }

    public Face getFace() {
        return face;
    }
}
