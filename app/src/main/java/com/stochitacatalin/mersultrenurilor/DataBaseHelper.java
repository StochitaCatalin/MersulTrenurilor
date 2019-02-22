package com.stochitacatalin.mersultrenurilor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "mersultrenurilor.sqlite";
    private static final String DBLOCATION = "/data/data/com.stochitacatalin.mersultrenurilor/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public DataBaseHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
        try{
            prepareDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void prepareDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            copyDatabase();
        }
    }

    private boolean checkDataBase(){
        boolean checkDB = false;
        try{
            String outFileName = DataBaseHelper.DBLOCATION + DataBaseHelper.DBNAME;
            File dbFile = new File(outFileName);
            checkDB = dbFile.exists();
        }catch (SQLiteException ignored){}
        return checkDB;
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = mContext.getAssets().open(DataBaseHelper.DBNAME);
        String outFileName = DataBaseHelper.DBLOCATION;
        File dbFile = new File(outFileName,DataBaseHelper.DBNAME);
        File path = new File(outFileName);
        try {
            dbFile.createNewFile();
        }catch (Exception e){
            path.mkdirs();
            dbFile.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(dbFile);
        byte[] buff = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buff)) > 0) {
            outputStream.write(buff, 0, length);
        }
        outputStream.flush();
        outputStream.close();
    }

    private void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if(mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void closeDatabase() {
        if(mDatabase!=null) {
            mDatabase.close();
        }
    }

    public Statie[] getStatii(String wtf, int limit){
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT id _id,nume FROM `statii` WHERE replace(replace(replace(replace(REPLACE(replace(nume,'ş','s'),'â','a'),'ă','a'),'Ş','S'),'Ţ','T'),'ţ','t') like '%"+wtf+"%' order by nume limit "+limit,null);
        Statie[] res = new Statie[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()){
            res[i] = new Statie(cursor.getInt(0),cursor.getString(1));
            i++;
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return res;
    }

    public Tren[] getTrenuri(String wtf,int limit){
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT numar FROM `trenuri` WHERE numar like '"+wtf+"%' order by numar limit "+limit,null);
        Tren[] res = new Tren[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()){
            res[i] = new Tren(cursor.getString(0));
            i++;
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return res;
    }

    public ArrayList<TrenRuta> getTrenFromTo(Statie from, Statie to){
        ArrayList<TrenRuta> a = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select c.categorie,c.servicii,a.tren,a.oraSosire,a.oraPlecare,a.tipOprire,b.oraSosire,b.oraPlecare,b.tipOprire from trasa a,trasa b,trenuri c where a.tren = b.tren and a.statie = "+from.getId()+" and b.statie = "+to.getId()+" and a.secventa < b.secventa and not a.tipOprire = 'N' and not b.tipOprire = 'N' and c.numar = a.tren order by a.oraPlecare",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            a.add(new TrenRuta(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),from,cursor.getInt(4),cursor.getString(5),cursor.getInt(6),to,cursor.getInt(7),cursor.getString(8)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return a;
    }
    public ArrayList<Ruta> getTren2FromTo(Statie from,Statie to){
        class Mr{
            String origine,statie,destinatie;
            int asteptare;
            public Mr(String origine, String statie, String destinatie,int asteptare) {
                this.origine = origine;
                this.statie = statie;
                this.destinatie = destinatie;
                this.asteptare = asteptare;
            }
        }
        ArrayList<Mr> mr = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select oritren,statie,desttren,dif from (select *,(case when top-tos >= 0 then top-tos else 86400 +(top - tos) end)dif from (Select trasa.tren oritren,trasa.statie,trasa.oraSosire%86400 tos from trasa,(select * from trasa where statie = "+from.getId()+" and tipOprire != 'N') ori where trasa.tren = ori.tren and trasa.secventa > ori.secventa and trasa.tipOprire != 'N') ori,(Select trasa.tren desttren,trasa.statie,trasa.oraPlecare%86400 top from trasa,(select * from trasa where statie = "+to.getId()+" and tipOprire != 'N') dest where trasa.tren = dest.tren and trasa.secventa < dest.secventa and trasa.tipOprire !='N') dest where ori.statie = dest.statie and oritren != desttren order by oritren, dif desc) group by oritren",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            mr.add(new Mr(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        ArrayList<Ruta> a = new ArrayList<>();
        System.out.println("LISTA BLA:"+mr.size());
        for(Mr m:mr){
            TrenRuta or = getTrenRuta(m.origine, String.valueOf(from.getId()),m.statie);
            TrenRuta de = getTrenRuta(m.destinatie,m.statie, String.valueOf(to.getId()));
            if(or != null && de != null){
                a.add(new Ruta(or,de,m.asteptare));
            }
        }
        return a;
    }
    public TrenRuta getTrenRuta(String numar,String from, String to){
        TrenRuta a = null;
        openDatabase();
        Cursor cursor;
        if(from == null || to == null) {
            cursor = mDatabase.rawQuery("select categorie,servicii,oraSosire,statie,nume,oraPlecare,tipOprire from trasa,statii,trenuri where numar = tren and tren = "+numar+" and not tipOprire = 'N' and statie = id", null);
            if(cursor.getCount() < 2)
                return null;
            cursor.moveToFirst();
            Object v[] = new Object[]{cursor.getString(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6)};
            cursor.moveToLast();
            a = new TrenRuta((String) v[0],(String)v[1],numar, (int)v[2],new Statie((int)v[3],(String)v[4]),(int)v[5],(String)v[6], cursor.getInt(2), new Statie(cursor.getInt(3),cursor.getString(4)), cursor.getInt(5), cursor.getString(6));
            cursor.moveToNext();
        }else {
            cursor = mDatabase.rawQuery("select f.categorie,f.servicii,a.tren,a.oraSosire,c.id,c.nume n1,a.oraPlecare,a.tipOprire,b.oraSosire,d.id,d.nume n2,b.oraPlecare,b.tipOprire from trasa a,trasa b,statii c,statii d,trenuri f where a.tren = "+numar+" and b.tren = a.tren and a.statie = "+from+" and b.statie = "+to+" and a.secventa < b.secventa and not a.tipOprire = 'N' and not b.tipOprire = 'N' and a.statie = c.id and b.statie = d.id and numar = a.tren order by a.oraPlecare", null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                a = new TrenRuta(cursor.getString(0),cursor.getString(1),cursor.getString(2), cursor.getInt(3), new Statie(cursor.getInt(4),cursor.getString(5)), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), new Statie(cursor.getInt(9),cursor.getString(10)), cursor.getInt(11), cursor.getString(12));
                cursor.moveToNext();
            }
        }
        cursor.close();
        closeDatabase();
        return a;
    }
    public TrenTrasa getTrenTrasa(TrenRuta tren){
        ArrayList<ElementTrasa> t = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT oraSosire,statie,nume,oraPlecare,tipOprire FROM trasa,statii where not tipOprire = 'N' and tren = '"+tren.getTren()+"' and statie = id",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int statie = cursor.getInt(1);
            t.add(new ElementTrasa(cursor.getInt(0),new Statie(cursor.getInt(1),cursor.getString(2)),cursor.getInt(3),cursor.getString(4), statie==tren.getFstatie().getId() || statie == tren.getTstatie().getId()));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return new TrenTrasa(tren,t);
    }
    public void insertIntarziere(String tren,String lat,String lon,String time,int accuracy){
        openDatabase();

        mDatabase.execSQL("INSERT INTO intarzieritrenuri (idtren, lat, lon,time,acuracy)\n" +
                "VALUES ('"+tren+"',"+lat+", "+lon+", '"+time+"',"+accuracy+")");
        //Cursor cursor = mDatabase.rawQuery(,null);
        //cursor.close();
        closeDatabase();
    }
    public int getIntarzieriSize(){
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("Select * from intarzieritrenuri",null);
        int sz = 0;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            sz++;
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return sz;
    }
    public Intarziere getIntarziere(){
        openDatabase();
        Intarziere intarziere = null;
        Cursor cursor = mDatabase.rawQuery("Select * from intarzieritrenuri",null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            intarziere = new Intarziere(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return intarziere;
    }
    public void removeIntarziere(int id){
        openDatabase();
        mDatabase.execSQL("DELETE FROM intarzieritrenuri WHERE id = "+id);
        closeDatabase();
    }

    public boolean workingTrain(String tren,String format) {
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM `restrictii` WHERE tren = '"+tren+"' and '"+format+"' BETWEEN dela and pinala and substr(zile,case cast(strftime('%w', '"+format+"') as Integer)when 0 then 7 else cast(strftime('%w', '"+format+"') as Integer) end,1) = '1'",null);
        cursor.moveToFirst();
        boolean working = false;
        if(!cursor.isAfterLast())
            working = true;
        cursor.close();
        closeDatabase();
        return working;
    }
}
