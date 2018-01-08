package com.pw.box.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pw.box.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by danger on
 * 2016/08/23
 * 功能:
 */
public class PatternLockView extends View {

    int xPtCount = 3;
    int yPtCount = 3;

    int xSpacing = 1;
    int ySpacing = 1;

    int innerCircleSize = 10;
    int innerCircleColor = 0xffffffff;

    int outerCircleSize1 = -1;
    int outerCircleSize2 = -1;
    int outerCircleLineWidth = 115;
    int outerCircleColor = 0xff999999;

    int lineColor1 = 0x99999999;
    int lineColor2 = 0x99999999;
    int lineColor3 = 0x99999999;
    int lineColor4 = 0x99999999;
    int lineWidth = 6;

    RectF rectF = new RectF();
    List<List<Cell>> patterns = new ArrayList<>();
    PointF lastMovePosition;
    OnPatternListener patternListener;
    private Paint circlePaint;
    private boolean mInputEnabled = true;

    public PatternLockView(Context context) {
        super(context);
        init(context, null);
    }

    public PatternLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        circlePaint = new Paint();


        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.PatternLockView);

            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.PatternLockView_xPtCount:
                        xPtCount = a.getInteger(attr, xPtCount);
                        break;
                    case R.styleable.PatternLockView_yPtCount:
                        yPtCount = a.getInteger(attr, yPtCount);
                        break;
                    case R.styleable.PatternLockView_xSpacing:
                        xSpacing = a.getDimensionPixelSize(attr, xSpacing);
                        break;
                    case R.styleable.PatternLockView_ySpacing:
                        ySpacing = a.getDimensionPixelSize(attr, ySpacing);
                        break;
                    case R.styleable.PatternLockView_innerCircleSize:
                        innerCircleSize = a.getDimensionPixelSize(attr, innerCircleSize);
                        break;
                    case R.styleable.PatternLockView_innerCircleColor:
                        innerCircleColor = a.getColor(attr, innerCircleColor);
                        break;
                    case R.styleable.PatternLockView_outerCircleSize1:
                        outerCircleSize1 = a.getDimensionPixelSize(attr, outerCircleSize1);
                        break;
                    case R.styleable.PatternLockView_outerCircleSize2:
                        outerCircleSize2 = a.getDimensionPixelSize(attr, outerCircleSize2);
                        break;
                    case R.styleable.PatternLockView_outerCircleColor:
                        outerCircleColor = a.getColor(attr, outerCircleColor);
                        break;
                    case R.styleable.PatternLockView_lineWidth:
                        lineWidth = a.getDimensionPixelSize(attr, lineWidth);
                        break;
                    case R.styleable.PatternLockView_lineColor1:
                        lineColor1 = a.getColor(attr, lineColor1);
                        break;
                    case R.styleable.PatternLockView_lineColor2:
                        lineColor2 = a.getColor(attr, lineColor2);
                        break;
                    case R.styleable.PatternLockView_lineColor3:
                        lineColor3 = a.getColor(attr, lineColor3);
                        break;
                    case R.styleable.PatternLockView_lineColor4:
                        lineColor4 = a.getColor(attr, lineColor4);
                        break;
                    case R.styleable.PatternLockView_outerCircleLineWidth:
                        outerCircleLineWidth = a.getDimensionPixelSize(attr, outerCircleLineWidth);
                        break;
                }
            }
            a.recycle();
        }

        // test
        /*Random r = new Random();
        for (int i = 0; i < 3; i++) {
            patterns.add(new Cell(
                    Math.abs(r.nextInt() % xPtCount),
                    Math.abs(r.nextInt() % yPtCount)));
        }*/

        // test
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        int contentW = xPtCount * outerCircleSize2 + (xPtCount - 1) * xSpacing;
        return contentW + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        int contentH = yPtCount * outerCircleSize2 + (yPtCount - 1) * ySpacing;
        return contentH + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);

        /* switch (mAspect) {
            case ASPECT_SQUARE:
                viewWidth = viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_WIDTH:
                viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_HEIGHT:
                viewWidth = Math.min(viewWidth, viewHeight);
                break;
        } */
        // if(L.E) L.get().v(TAG, "LockPatternView dimensions: " + viewWidth + "x" + viewHeight);
        setMeasuredDimension(viewWidth, viewHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


    }

    boolean have(int x, int y) {
        float lastX = 0, lastY = 0;

        for (List<Cell> list : patterns) {

            for (Cell cell : list) {
                if (cell.x == x && cell.y == y) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        circlePaint.setAntiAlias(true);
        // draw circles

        int rad1 = innerCircleSize / 2;
        int rad2 = outerCircleSize1 / 2;
        for (int i = 0; i < yPtCount; i++) {
            // float topY = paddingTop + i * squareHeight;
            //float centerY = mPaddingTop + i * mSquareHeight + (mSquareHeight / 2);
            float centerY = getPaddingTop() + i * (outerCircleSize2 + ySpacing) + outerCircleSize2 / 2;
            for (int j = 0; j < xPtCount; j++) {
                float centerX = getPaddingLeft() + j * (outerCircleSize2 + xSpacing) + outerCircleSize2 / 2;

                rectF.set(centerX - rad1, centerY - rad1, centerX + rad1, centerY + rad1);
                circlePaint.setColor(innerCircleColor);
                circlePaint.setStyle(Paint.Style.FILL);
                canvas.drawOval(rectF, circlePaint);

                if (have(j, i)) {
                    rectF.set(centerX - rad2, centerY - rad2, centerX + rad2, centerY + rad2);
                    circlePaint.setColor(outerCircleColor);
                    // circlePaint.setARGB(255, 212, 225, 233);
                    circlePaint.setStrokeWidth((outerCircleSize2 - outerCircleSize1) / 2.0f);

                    circlePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawOval(rectF, circlePaint);
                }
            }
        }


        float lastX = -1, lastY = -1;
        int lineColors[] = {lineColor1, lineColor2, lineColor3, lineColor4};
        for (int m = 0; m < patterns.size(); m++) {
            List<Cell> list = patterns.get(m);
            final int count = list.size();


            // Path path = new Path();
            for (int i = 0; i < count; i++) {
                Cell cell = list.get(i);
                int lineColor = lineColors[Math.min(m, lineColors.length - 1)];
                float centerX = getPaddingLeft() + cell.x * (outerCircleSize2 + xSpacing) + outerCircleSize2 / 2;
                float centerY = getPaddingTop() + cell.y * (outerCircleSize2 + ySpacing) + outerCircleSize2 / 2;

                circlePaint.setColor(lineColor);
                // circlePaint.setAlpha(255 - m * 25);
                circlePaint.setStrokeWidth(lineWidth);

                if (i > 0) {
                    // path.moveTo(centerX, centerY);
                    canvas.drawLine(lastX, lastY, centerX, centerY, circlePaint);
                } /*else {
                    // path.lineTo(centerX, centerY);
                }*/
                lastX = centerX;
                lastY = centerY;

                // circlePaint.ef(Paint.Style.STROKE);
                // rectF.set(centerX - rad1, centerY - rad1, centerX + rad1, centerY + rad1);
                int radFcs = rad1 * 3 / 2;
                rectF.set(centerX - radFcs, centerY - radFcs, centerX + radFcs, centerY + radFcs);

                lineColor |= 0xff000000;
                circlePaint.setColor(lineColor);
                circlePaint.setStyle(Paint.Style.FILL);
                canvas.drawOval(rectF, circlePaint);
            }

            // circlePaint.setStyle(Paint.Style.STROKE);
            // canvas.drawPath(path, circlePaint);
        }
        if (lastMovePosition != null) {
            canvas.drawLine(lastX, lastY, lastMovePosition.x, lastMovePosition.y, circlePaint);
        }


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInputEnabled || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                return true;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                return true;
            case MotionEvent.ACTION_CANCEL:
                resetPattern();
                notifyPatternCleared();
                return true;
        }
        return false;
    }


    private void notifyPatternCleared() {
        if (patternListener != null) {
            patternListener.onPatternCleared();
        }
    }

    private void notifyPatternDetected() {
        if (patternListener != null) {
            patternListener.onPatternDetected(patterns);
        }
    }

    public void resetPattern() {
        patterns.clear();
        invalidate();
        notifyPatternCleared();
    }

    private void handleActionMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final Cell hitCell = detectAndAddHit(x, y);

        lastMovePosition = new PointF(event.getX(), event.getY());

        if (hitCell == null) {
            return;
        }
        List<Cell> cells;
        if (patterns.isEmpty()) {
            cells = new ArrayList<>();
            patterns.add(cells);
        } else {
            cells = patterns.get(patterns.size() - 1);
        }

        if (!cells.contains(hitCell)) {
            cells.add(hitCell);
        }

        invalidate();
    }

    private void handleActionUp(MotionEvent event) {
        lastMovePosition = null;
        invalidate();
        if (patterns.size() > 0) {
            notifyPatternDetected();
        }

    }

    private void handleActionDown(MotionEvent event) {

        // patterns.clear();
        final float x = event.getX();
        final float y = event.getY();

        final Cell hitCell = detectAndAddHit(x, y);

        if (patterns.size() > 0 && patterns.get(patterns.size() - 1).size() <= 1) {
            patterns.get(patterns.size() - 1).clear();
        } else {
            patterns.add(new ArrayList<Cell>());
        }
        if (hitCell != null) {
            patterns.get(patterns.size() - 1).add(hitCell);
        }

        invalidate();
    }

    private Cell detectAndAddHit(float x, float y) {

        if (x < getPaddingLeft() || x > getWidth() - getPaddingRight()
                || y < getPaddingTop() || y > getHeight() - getPaddingBottom()) {
            return null;
        }

        int xCellD = (int) ((x - getPaddingLeft()) % (outerCircleSize2 + xSpacing));
        int yCellD = (int) ((y - getPaddingTop()) % (outerCircleSize2 + ySpacing));

        if (xCellD >= outerCircleSize2 || yCellD >= outerCircleSize2) {
            return null;
        }
        int xCell = (int) ((x - getPaddingLeft()) / (outerCircleSize2 + xSpacing));
        int yCell = (int) ((y - getPaddingTop()) / (outerCircleSize2 + ySpacing));

        if (xCell >= xPtCount || yCell >= yPtCount) {
            return null;
        }
        return new Cell(xCell, yCell);
    }


    public void setPatternListener(OnPatternListener patternListener) {
        this.patternListener = patternListener;
    }

    public List<List<Cell>> getPatterns() {
        return patterns;
    }

    public int getPointCount(List<List<Cell>> patterns) {
        int count = 0;
        for (List<Cell> list : patterns) {
            count += list.size();
        }
        return count;
    }

    public int getPointCount() {
        return getPointCount(patterns);
    }

    public String toString(List<List<Cell>> patterns) {
        StringBuilder sb = new StringBuilder();

        for (List<Cell> list : patterns) {
            for (Cell cell : list) {
                sb.append(String.format("%x%x", cell.x, cell.y));
            }
            sb.append("|");
        }

        return sb.toString();
    }

    /**
     * The call back interface for detecting patterns entered by the user.
     */
    public interface OnPatternListener {
        /**
         * The pattern was cleared.
         */
        void onPatternCleared();

        /**
         * A pattern was detected from the user.
         */
        void onPatternDetected(List<List<Cell>> p);
    }

    /**
     * Represents a cell in the 3 X 3 matrix of the unlock pattern view.
     */
    public static class Cell {
        int x;
        int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "(" + x + "," + y + ")";
        }

        @Override
        public boolean equals(Object other) {
            Cell o = (Cell) other;
            return x == o.x && y == o.y;
        }
    }
}
