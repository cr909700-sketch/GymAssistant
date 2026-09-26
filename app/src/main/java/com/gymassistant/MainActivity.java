package com.gymassistant;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root, content;
    private SharedPreferences prefs;
    private final int purple = Color.rgb(124,77,255);
    private final int bg = Color.rgb(18,18,18);
    private final int card = Color.rgb(30,30,30);
    private final int text = Color.WHITE;
    private final int muted = Color.rgb(185,185,185);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("gym", MODE_PRIVATE);
        showHome();
    }

    private TextView title(String s, int size) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextColor(text);
        v.setTextSize(size);
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setPadding(0, 8, 0, 12);
        return v;
    }

    private TextView label(String s) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextColor(muted);
        v.setTextSize(14);
        v.setPadding(0, 8, 0, 6);
        return v;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setBackgroundColor(purple);
        return b;
    }

    private void base(String pageTitle) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);
        root.setPadding(24, 24, 24, 16);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView t = title(pageTitle, 25);
        top.addView(t, new LinearLayout.LayoutParams(0, -2, 1));

        Button home = new Button(this);
        home.setText("Inicio");
        home.setAllCaps(false);
        home.setTextColor(Color.WHITE);
        home.setBackgroundColor(Color.TRANSPARENT);
        home.setOnClickListener(v -> showHome());
        top.addView(home);

        root.addView(top);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private void addSpace(int dp) {
        Space s = new Space(this);
        content.addView(s, new LinearLayout.LayoutParams(1, dp));
    }

    private void addCard(String heading, String body) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(18, 16, 18, 16);
        box.setBackgroundColor(card);

        TextView h = title(heading, 18);
        TextView b = label(body);
        b.setTextSize(15);
        b.setTextColor(text);

        box.addView(h);
        box.addView(b);
        content.addView(box, new LinearLayout.LayoutParams(-1, -2));
        addSpace(12);
    }

    private void showHome() {
        base("Gym Assistant");
        content.addView(title("Tu entrenamiento, simple.", 28));
        content.addView(label("Generá una rutina adaptada a tu nivel, objetivo y días disponibles."));
        addSpace(14);

        String level = prefs.getString("level", "");
        if (level.isEmpty()) {
            addCard("Primer paso", "Configurá tu perfil y la app generará tu primera rutina.");
            Button start = button("Crear mi rutina");
            start.setOnClickListener(v -> showSetup());
            content.addView(start, new LinearLayout.LayoutParams(-1, 58));
        } else {
            String goal = prefs.getString("goal", "Hipertrofia");
            int days = prefs.getInt("days", 3);
            addCard("Tu perfil", "Nivel: " + level + "\nObjetivo: " + goal + "\nDías por semana: " + days);

            Button routine = button("Ver mi rutina");
            routine.setOnClickListener(v -> showRoutine());
            content.addView(routine, new LinearLayout.LayoutParams(-1, 58));

            addSpace(10);

            Button edit = button("Cambiar perfil");
            edit.setOnClickListener(v -> showSetup());
            content.addView(edit, new LinearLayout.LayoutParams(-1, 58));
        }
    }

    private void showSetup() {
        base("Crear perfil");
        content.addView(title("Contame sobre vos", 26));
        content.addView(label("Elegí las opciones que más se parezcan a vos."));
        addSpace(8);

        Spinner level = spinner(new String[]{"Principiante", "Intermedio", "Avanzado"});
        Spinner goal = spinner(new String[]{"Hipertrofia", "Fuerza", "Pérdida de grasa", "Rendimiento general"});
        Spinner days = spinner(new String[]{"1", "2", "3", "4", "5", "6"});
        Spinner duration = spinner(new String[]{"30 minutos", "45 minutos", "60 minutos", "75 minutos", "90 minutos"});

        content.addView(label("Nivel"));
        content.addView(level);
        content.addView(label("Objetivo"));
        content.addView(goal);
        content.addView(label("Días por semana"));
        content.addView(days);
        content.addView(label("Tiempo disponible"));
        content.addView(duration);
        addSpace(14);

        Button save = button("Generar rutina");
        save.setOnClickListener(v -> {
            prefs.edit()
                .putString("level", level.getSelectedItem().toString())
                .putString("goal", goal.getSelectedItem().toString())
                .putInt("days", Integer.parseInt(days.getSelectedItem().toString()))
                .putString("duration", duration.getSelectedItem().toString())
                .apply();
            showRoutine();
        });
        content.addView(save, new LinearLayout.LayoutParams(-1, 58));
    }

    private Spinner spinner(String[] items) {
        Spinner s = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            items
        );
        s.setAdapter(a);
        return s;
    }

    private void showRoutine() {
        base("Mi rutina");

        int days = prefs.getInt("days", 3);
        String level = prefs.getString("level", "Principiante");
        String goal = prefs.getString("goal", "Hipertrofia");

        content.addView(title(days + " días • " + goal, 24));
        content.addView(label("Nivel " + level + " • rutina generada automáticamente"));
        addSpace(8);

        String[] splits;
        if (days == 1) splits = new String[]{"Full Body"};
        else if (days == 2) splits = new String[]{"Full Body A", "Full Body B"};
        else if (days == 3) splits = new String[]{"Tren superior", "Tren inferior", "Full Body"};
        else if (days == 4) splits = new String[]{"Superior A", "Inferior A", "Superior B", "Inferior B"};
        else if (days == 5) splits = new String[]{"Push", "Pull", "Legs", "Upper", "Lower"};
        else splits = new String[]{"Push A", "Pull A", "Legs A", "Push B", "Pull B", "Legs B"};

        for (int i = 0; i < splits.length; i++) {
            final int day = i;
            addCard("Día " + (i + 1) + " — " + splits[i], exercisesFor(i, level));

            Button open = button("Empezar día " + (i + 1));
            open.setOnClickListener(v -> showSession(day, splits[day]));
            content.addView(open, new LinearLayout.LayoutParams(-1, 52));
            addSpace(10);
        }
    }

    private String exercisesFor(int day, String level) {
        String[][] bank = {
            {"Sentadilla / prensa", "Press de banca", "Remo", "Peso muerto rumano", "Elevaciones laterales", "Abdominales"},
            {"Press inclinado", "Jalón al pecho", "Prensa", "Curl femoral", "Curl de bíceps", "Tríceps"},
            {"Press militar", "Dominadas/jalón", "Sentadilla búlgara", "Hip thrust", "Elevaciones laterales", "Gemelos"}
        };

        StringBuilder s = new StringBuilder();
        String[] list = bank[day % bank.length];

        for (String e : list) {
            s.append("• ").append(e).append(" — 3 series\n");
        }

        if (level.equals("Principiante")) {
            s.append("\nDescansá 60–120 s y priorizá la técnica.");
        } else {
            s.append("\nDejá 1–3 repeticiones en reserva en la mayoría de las series.");
        }

        return s.toString().trim();
    }

    private void showSession(int day, String name) {
        base("Entrenamiento");
        content.addView(title(name, 25));
        content.addView(label("Marcá cada ejercicio al terminar."));
        addSpace(8);

        String[] ex = {
            "Calentamiento",
            "Ejercicio principal",
            "Segundo ejercicio",
            "Accesorio 1",
            "Accesorio 2",
            "Core / final"
        };

        for (String e : ex) {
            CheckBox c = new CheckBox(this);
            c.setText(e + " • 3 series");
            c.setTextColor(text);
            c.setTextSize(16);
            c.setPadding(0, 12, 0, 12);
            content.addView(c);
        }

        addSpace(12);

        Button finish = button("Terminar entrenamiento");
        finish.setOnClickListener(v -> {
            Toast.makeText(this, "¡Entrenamiento terminado! Buen trabajo.", Toast.LENGTH_LONG).show();
            showRoutine();
        });
        content.addView(finish, new LinearLayout.LayoutParams(-1, 58));
    }
}
