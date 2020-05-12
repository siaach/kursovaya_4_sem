package com.example.currency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class ConverterActivity extends AppCompatActivity {

    private Document doc;
    private Thread SecondThread;
    private Runnable runnable;
    private static ArrayList <Currency> list = new ArrayList<>();
    private static ArrayList <String> list_for_spinner = new ArrayList<>();
    private Spinner spinner1;
    private Spinner spinner2;

    TextView resultField;
    EditText numberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        resultField =(TextView) findViewById(R.id.resultField);
        numberField = (EditText) findViewById(R.id.numberField);

        //Вызов функции, для считывания данных с сайта и записи в ArrayList
        init();

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_for_spinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_for_spinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);


    }

    //обработка нажатия кнопки для перехода к главному активити
    public void onClickMain(View view) {
        Intent intent = new Intent(ConverterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //обработка нажатия кнопки для конвертации валют
    public void onClickConvert(View view) {

        String number = numberField.getText().toString(); //считанная сумма
        double sum = Double.parseDouble(number.trim()); // сумма введенная пользователем для перевода
        Log.d("MyLog", "Введенная пользователем сумма начальной валюты:  " + sum);

        double val_begin = 0; // курс начальной валюты
        double val_end = 0; // курс начальной валюты

        //запись курса выбранных валют из списка
        int index = 0;

        index = spinner1.getSelectedItemPosition();
        val_begin = list.get(index).getValue();
        Log.d("MyLog", "Индекс начальной валюты:  " + index);
        Log.d("MyLog", "Курс начальной валюты:  " + val_begin);

        index = spinner2.getSelectedItemPosition();
        val_end = list.get(index).getValue();
        Log.d("MyLog", "Индекс конечной валюты:  " + index);
        Log.d("MyLog", "Курс конечной валюты:  " + val_end);

        // перевод в рубли
        double res_rub = 0;
        res_rub = sum * val_begin;
        Log.d("MyLog", "Промежуточный результат:  " + res_rub);


        // перевод в конечную валюту
        double res_val = 0;
        res_val = res_rub / val_end;

        Log.d("MyLog", "Конечный результат:  " + res_val);
        resultField.setText(Double.toString(res_val));
    }


    //запуск второго потока
    private void init () {

        list.clear();
        list_for_spinner.clear();
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

            for (int i = 1; i < currency.childrenSize(); i++) {
                String s1 = currency.children().get(i).child(2).text();
                String s2 = currency.children().get(i).child(4).text();
                String s3 = currency.children().get(i).child(1).text();

                String s2_p = s2.replace(",", ".");

                int x = Integer.parseInt(s1.trim());
                double y = Double.parseDouble(s2_p.trim());

                Currency C = new Currency(i-1, s3, y/x);
                list.add(C);
                list_for_spinner.add(s3);


            }
            Currency C = new Currency(list.size(), "RUB", 1);
            list.add(C);
            list_for_spinner.add("RUB");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
