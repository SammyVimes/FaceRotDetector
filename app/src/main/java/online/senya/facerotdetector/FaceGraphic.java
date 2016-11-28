package online.senya.facerotdetector;

/**
 * Created by dsv on 22.11.16.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

import online.senya.facerotdetector.ui.camera.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
    private final String[] anglesEndings = {"градус", "градуса", "градусов"};
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    private String endingGen(final int number, final String[] titles) {
        int[] cases = {2, 0, 1, 1, 1, 2};
        return titles[(number % 100 > 4 && number % 100 < 20) ? 2 : cases[(number % 10 < 5) ? number % 10 : 5]];
    }


    int height = 640;
    int width = 480;

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        int cW = canvas.getWidth();
        int cH = canvas.getHeight();

        // модификатор сдвига (камера снимает 640*480, а канвас другого размера)
        float xMod = (float) cW / width;
        float yMod = (float) cH / height;


        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);

        final List<Landmark> landmarks = face.getLandmarks();

        Landmark leftEye = null;
        Landmark rightEye = null;

        for (Landmark landmark : landmarks) {
            int cx = (int) (landmark.getPosition().x);
            int cy = (int) (landmark.getPosition().y);
            if (landmark.getType() == Landmark.LEFT_EYE) {
                leftEye = landmark;
            }
            if (landmark.getType() == Landmark.RIGHT_EYE) {
                rightEye = landmark;
            }
        }

        if (leftEye != null && rightEye != null) {
//            float lX = (int) (leftEye.getPosition().x) * xMod;
//            float lY = (int) (leftEye.getPosition().y) * yMod;
//            float rX = (int) (rightEye.getPosition().x) * xMod;
//            float rY = (int) (rightEye.getPosition().y) * yMod;
            float lX = translateX(leftEye.getPosition().x);
            float lY = translateY(leftEye.getPosition().y);
            float rX = translateX(rightEye.getPosition().x);
            float rY = translateY(rightEye.getPosition().y);
            canvas.drawCircle(lX, lY, 10, mIdPaint);
            canvas.drawCircle(rX, rY, 10, mIdPaint);
            canvas.drawLine(lX, lY, rX, rY, mIdPaint);
        }


        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}