package com.br.medialert.dao;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.support.annotation.NonNull;
        import android.util.Log;

        import java.util.ArrayList;
        import java.util.List;

        import com.br.medialert.Controle;
        import com.br.medialert.model.Captura;

public class CapturaDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "IoT.db";
    private static final int DATABASE_VERSION = 2;

    public CapturaDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE CAPTURAS(" +
                "timeStamp TEXT NOT NULL," +
                "devCode INTEGER," +
                "luz INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CapturaDAO.class.getName(),
                "Atualizando o database da versão " + oldVersion + " para "
                        + newVersion + ", apagando todos os dados antigos.");

        String sql = "DROP TABLE IF EXISTS CAPTURAS";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insere(Captura Captura) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = getDadosDoCaptura(Captura);
        db.insert("CAPTURAS",null, dados);
    }

/*
    public void altera(Captura Captura) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = getDadosDoCaptura(Captura);
        String[] params = {Captura.getId().toString()};
        db.update("Capturas", dados, "id = ?", params);
    }
*/

    public List<Captura> buscaCapturas() {
        String sql = "SELECT * FROM CAPTURAS";
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.rawQuery(sql, null);

            List<Captura> Capturas = new ArrayList<>();

            while (c.moveToNext()) {
                Captura Captura = new Captura();
                Captura.setTimestamp(c.getString(c.getColumnIndex("timeStamp")));
                Captura.setDevCode(c.getInt(c.getColumnIndex("devCode")));
                Captura.setLuz(c.getInt(c.getColumnIndex("luz")));
                Capturas.add(Captura);
            }
            c.close();
            return Capturas;
        }catch(Exception e){
            Log.e("Erro ao ler o banco: ", e.getMessage());
        }
        return null;
    }

/*
    public void deleta(Captura Captura) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {Captura.getId().toString()};
        db.delete("Capturas", "id = ?", params);
    }*/


    @NonNull
    private ContentValues getDadosDoCaptura(Captura Captura) {
        ContentValues dados = new ContentValues();
        dados.put("timestamp", Captura.getTimestamp());
        dados.put("devCode", Captura.getDevCode());
        dados.put("luz", Captura.getLuz());
        return dados;
    }
}
