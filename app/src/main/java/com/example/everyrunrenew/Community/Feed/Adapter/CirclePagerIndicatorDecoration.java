package com.example.everyrunrenew.Community.Feed.Adapter;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.BlockingDeque;

public class CirclePagerIndicatorDecoration  extends RecyclerView.ItemDecoration{
    private final String TAG = this.getClass().getSimpleName();
    private int colorActive = 0xFFFFFFFF;
    private int colorInactive = 0x66FFFFFF;

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private final int mIndicatorHeight = (int) (DP * 16);

    /**
     * Indicator stroke width.
     */
    private final float mIndicatorStrokeWidth = DP * 2;

    /**
     * Indicator width.
     */
    private final float mIndicatorItemLength = DP * 16;
    /**
     * Padding between indicators.
     */
    private final float mIndicatorItemPadding = DP * 4;

    /**
     * Some more natural animation interpolation
     */
    private final android.view.animation.Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    // 그리기(Draw)하기 위해 쓰여지는 도구 이다. 쉽게 말해 Canvas가 도화지라면 Pain는 붓이라고 한다. 도화지에 그림을 그릴 떄 우리들은 여러가지 붓을 사용하여 효과를 준다. 붓을 굵기, 색상, 모양 등
    private final Paint mPaint = new Paint();

    Canvas canvas = new Canvas();

    public CirclePagerIndicatorDecoration() {
        // 선의 끝나는 지점의 장시을 설정
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 동근 모양
        // pain의 굵기를 설정
        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
        // pain 스타일 지정
        mPaint.setStyle(Paint.Style.FILL); // fill 색상이 채워지고 테두리는 그려지지 않는다.
        // pain의 경계면을 부드럽게 처리한다.
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(10);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        //사진갯수
        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate width and subtract half from center
        // 가로로 중심을 잡고 너비를 계산하고 중심에서 반을 뺀다.
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

        // center vertically in the allotted space
        // 할당된 공간에 수직으로 중심을 잡는다.
        float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);

        // find active page (which should be highlighted)
        // 활성 페이지 찾기(강조되어야함)
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = layoutManager.findFirstVisibleItemPosition();
        Log.d(TAG, "onDrawOver: activePosition=" + activePosition);
        if (activePosition == RecyclerView.NO_POSITION) {
            return;
        }

        // find offset of active page (if the user is scrolling)
        // 활성 페이지의 오프셋 찾기(사용자가 스크롤 중인 경우)
        final View activeChild = layoutManager.findViewByPosition(activePosition);
        int left = activeChild.getLeft();
        int width = activeChild.getWidth();

        // on swipe the active item will be positioned from [-width, 0]
        // 스와이프시 활성 항목은 [-width, 0]에서 위치하게 된다.
        // interpolate offset for smooth animation
        // 부드러운 애니메이션을 위해 간격 띄우기
        float progress = mInterpolator.getInterpolation(left * -1 / (float) width);
        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount);

    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount) {
        mPaint.setColor(Color.GRAY);
        Log.d(TAG, "drawInactiveIndicators: inactive");
        // width of item indicator including padding
        // 패딩을 포함한 indicator 폭
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartX;

        //아이템이 한개면 버튼을 출력하지 않는다.
        if(itemCount > 1) {
            for (int i = 0; i < itemCount; i++) {
                // draw the line for every item
                c.drawCircle(start, indicatorPosY, itemWidth / 6, mPaint);
//              c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint);

                start += itemWidth;
                Log.d(TAG, "drawInactiveIndicators: start="+start);
            }
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY,
                                int highlightPosition, float progress, int itemCount) {
        mPaint.setColor(Color.BLACK);
        Log.d(TAG, "drawHighlights: active");
        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;
        Log.d(TAG, "drawHighlights: itemWidth=" +  itemWidth);
        if (progress == 0F) {
            // no swipe, draw a normal indicator
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;

//            c.drawLine(highlightStart, indicatorPosY,
//                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
//

            //아이템이 한개면 버튼을 출력하지 않는다.
            if(itemCount > 1) {
                
                c.drawCircle(highlightStart, indicatorPosY, itemWidth / 6, mPaint);
//                            c.drawLine(highlightStart, indicatorPosY,
//                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
            }
        } else {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            // calculate partial highlight
            float partialLength = mIndicatorItemLength * progress;

            // draw the cut off highlight
           /* c.drawLine(highlightStart + partialLength, indicatorPosY,
                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
*/
            // draw the highlight overlapping to the next item as well
            Log.d(TAG, "drawHighlights: itemcount="+itemCount);
            Log.d(TAG, "drawHighlights: highlightposition =" + highlightPosition);
            Log.d(TAG, "drawHighlights: 165 highlightStart =" + highlightStart);
            if ( highlightPosition < itemCount ) {
                Log.d(TAG, "drawHighlights:  167번 itemwidth =" + itemWidth);
                highlightStart += itemWidth;
                Log.d(TAG, "drawHighlights: 170 highlightPosition=" + highlightPosition);
//                c.drawLine(highlightStart, indicatorPosY,
//                        highlightStart + partialLength, indicatorPosY, mPaint);

                Log.d("dot", "drawHighlights: itemcount: " + itemCount);
                Log.d(TAG, "drawHighlights: highlightStart=" + highlightStart);
                c.drawCircle(highlightStart-70 ,indicatorPosY,itemWidth / 6,mPaint);

            }else{
                Log.d(TAG, "drawHighlights: 178번");
            }
        }
    }

//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
//        outRect.bottom = mIndicatorHeight;
//    }


}
