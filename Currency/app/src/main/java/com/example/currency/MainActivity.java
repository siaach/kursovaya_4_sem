package com.example.currency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //переменная для парсинга данных с сайта
    private Document doc;

    //второй поток, для подключения к интернету
    private Thread SecondThread;
    private Runnable runnable;

    //переменные для заполнения ListView в главном активити
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //парсинг данных и вывод в ListView
        init();

    }

    //обработка нажатия кнопки перехода к конвертеру
    public void onClickConverter(View view) {
        Intent intent = new Intent(MainActivity.this, ConverterActivity.class);
        startActivity(intent);
    }

    //запуск второго потока
    private void init () {
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this,R.layout.list_item_1, arrayList,getLayoutInflater());
        listView.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        SecondThread = new Thread(runnable);
        SecondThread.start();
        try {
            SecondThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    //парсинг данных с сайта ЦБР
    private void getWeb () {
        try {
            doc = Jsoup.connect("https://cbr.ru/currency_base/daily/").get();

            Elements tables = doc.getElementsByTag("tbody");
            Element currency = tables.get(0);


            for (int i = 0; i < currency.childrenSize(); i++) {

                ListItemClass items;
                items = new ListItemClass();
                items.setData_1(currency.children().get(i).child(1).text());
                items.setData_2(currency.children().get(i).child(2).text());
                items.setData_3(currency.children().get(i).child(3).text());
                items.setData_4(currency.children().get(i).child(4).text());
                arrayList.add(items);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
