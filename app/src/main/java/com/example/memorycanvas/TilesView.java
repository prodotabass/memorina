package com.example.memorycanvas;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

class Card {
    private Paint p = new Paint();
    int color, backColor = Color.DKGRAY;
    boolean isOpen = false;
    private float x, y, width, height;


    public Card(float x, float y, float width, float height, int color) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public boolean flip(float touchX, float touchY) {
        if (touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height) {
            isOpen = !isOpen;
            return true;
        } else return false;
    }

    public void draw(Canvas c) {
        //рисуем карту в виде цветного прямоугольника
        if (isOpen) {
            p.setColor(color);
        } else p.setColor(backColor);
        c.drawRect(x, y, x + width, y + height, p);
    }
}

public class TilesView extends View {
    int openedCard = 0;
    Card openCard;
      //TODO: изменить задержку, долгая
    final int PAUSE_LENGTH = 2;
    boolean isOnPause = false;
    int n = 4;
    static ArrayList<Card> listCards = new ArrayList<>();
    int widthCard = 200;
    int heightCard = 300;
    int distance = 55;

    int width, height; // ширина и высота канвы

    public TilesView(Context context) {
        super(context);
    }

    public TilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 1) заполнить массив tiles случайными цветами
        /*
//TODO:удалить очень интересный комментарий
        cards[0] = new Card(0, 0, 200, 300, Color.YELLOW);
        cards[1] = new Card(200 + 50, 0, 200 + 50, 300, Color.YELLOW);
        cards[2] = new Card(500 + 50, 0, 200 + 50, 300, Color.GREEN);
        cards[3] = new Card(800 + 50, 0, 200 + 50, 300, Color.GREEN);
        cards[4] = new Card(0, 300 + 50, 200, 300, Color.RED);
        cards[5] = new Card(200 + 50, 300 + 50, 200 + 50, 300, Color.RED);
        cards[6] = new Card(500 + 50, 300 + 50, 200 + 50, 300, Color.BLUE);
        cards[7] = new Card(800 + 50, 300 + 50, 200 + 50, 300, Color.BLUE);
        listCards = (new ArrayList<Card>(Arrays.asList(cards)));
         */

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) { //TODO упостить создание новых карт
                listCards.add(new Card((widthCard * j + distance * j) + distance, heightCard * i + distance * i, widthCard, heightCard, Color.LTGRAY));
                Collections.shuffle(listCards);
            }
        }
             //TODO: упроситть процедуру генерирования цветов
        for (int i = 0; i < listCards.size(); i += 2) {
            listCards.get(i).color = Color.rgb(i * 45 + 25, i * 10, i * 25 + 15);
            listCards.get(i + 1).color = Color.rgb(i * 45 + 25, i * 10, i * 25 + 15);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        // 2) отрисовка плиток
       //TODO: переменная p не используется
        Paint p = new Paint();
        p.setColor(Color.GREEN);

        for (Card c : listCards) {
            c.draw(canvas);
        }
        if (listCards.size() == 0) {
            Toast.makeText(getContext(), "Карты на столе закончились! Вы нашли все пары", Toast.LENGTH_SHORT).show(); //TODO:Упростим текст
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3) получить координаты касания
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 4) определить тип события
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPause) {
            // палец коснулся экрана
            for (Card c : listCards) {
                if (openedCard == 0) {
                    if (c.flip(x, y)) {
                        Log.d("mytag", "card flipped: " + openedCard);
                        openedCard++;
                        openCard = c;
                        invalidate();
                        return true;
                    }
                }
                if (openedCard == 1) {
                    // если открылись карты одинакого цвета, то удалить из списка иначе запустить задержку
                    // перевернуть карту с задержкой
                     //TODO: повторяющиеся команды, можно сделать проще
                    if (c.flip(x, y) && openCard != c) {
                        openedCard++;
                        if (openCard.color == c.color) {
                            invalidate();
                            PauseTask task = new PauseTask();
                            task.execute(0);
                            isOnPause = true;
                            listCards.remove(openCard);
                            listCards.remove(c);
                            return true;
                        } else {
                            // запуск задержки
                            invalidate();
                            PauseTask task = new PauseTask();
                            task.execute(PAUSE_LENGTH);
                            isOnPause = true;
                            return true;
                        }
                    }
                }
            }
            if (listCards.size() == 0) {
                Toast.makeText(getContext(), "Карты на столе закончились! Вы нашли все пары", Toast.LENGTH_SHORT).show();
            }

        }
        // 5) определить, какой из плиток коснулись
        // изменить её цвет на противоположный
        // 6) проверить, не выиграли ли вы (все плитки одного цвета)

        //invalidate(); // заставляет экран перерисоваться
        return true;
    }


    public void onClick() {
        listCards.clear();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {//TODO: тут также упрощаем создание новых карт
                listCards.add(new Card((widthCard * j + distance * j) + distance, heightCard * i + distance * i, widthCard, heightCard, Color.LTGRAY));
                Collections.shuffle(listCards);
            }
        }
        for (int i = 0; i < listCards.size(); i += 2) {
            listCards.get(i).color = Color.rgb(i * 30 + 20, i * 15, i * 20 + 5);
            listCards.get(i + 1).color = Color.rgb(i * 30 + 20, i * 15, i * 20 + 5);
        }
        invalidate();
        Toast.makeText(getContext(), "Карты переразданы!", Toast.LENGTH_SHORT).show();

    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            Log.d("tag", "pause started");
            try {
                Thread.sleep(integers[0] * 1000);
            } catch (InterruptedException e) {
                Log.e("error sleep", String.valueOf(e));
            }
            Log.d("tag", "pause finished");

            return null;
        }

        // после паузы перевернуть все карты
        @Override
        protected void onPostExecute(Void aVoid) {
            for (Card c : listCards) {
                if (c.isOpen) {
                    c.isOpen = false;
                }
            }
            openedCard = 0;
            isOnPause = false;
            invalidate();
        }

    }
}




