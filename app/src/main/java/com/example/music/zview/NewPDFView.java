package com.example.music.zview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.joanzapata.pdfview.PDFView;

public class NewPDFView extends PDFView {
    private onDisTouchListener onDisTouchListener;

    public void setOnDisTouchListener(NewPDFView.onDisTouchListener onDisTouchListener) {
        this.onDisTouchListener = onDisTouchListener;
    }

    /**
     * Construct the initial view
     *
     * @param context
     * @param set
     */
    public NewPDFView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        onDisTouchListener.onDisTouch(event);
        return super.dispatchTouchEvent(event);
    }

    public interface onDisTouchListener {
        void onDisTouch(MotionEvent event);
    }
}
