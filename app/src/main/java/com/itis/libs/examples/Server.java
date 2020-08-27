package com.itis.libs.examples;

import org.json.JSONArray;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Server {


    public static final String[]ROOT_SOURCE =
            {"Benson Idahosa","William the Conqueror", "Pastor E.A Adeboye","Donald Trump" , "King Arthur","Mike Tyson",
                    "Boris Yeltsin","William Tyndale","Bill Clinton","Donald Trump","Margaret Thatcher","Billy Graham",
                    "Michael Jackson","Albert Einstein","Oswald Chambers","Martin Luther King Jr.","Herbert Macaulay", "Mary Slessor",
                    "Ronald Reagan","Paul of Tarsus","Nelson Mandela", "Mahatma Gandhi","Bill Gates", "Brian Houston","T.D Jakes",
                    "Don Moen","Martin Luther", "Abraham Lincoln" , "Mother Theresa","Obafemi Awolowo", "Wole Soyinka","Nnamdi Azikiwe",
                    "Chinua Achebe", "Cyprian Ekwensi","Kwame Nkrumah","Richard the Lionheart","Cyrus the Great","Alexander the Great",
                    "Julius Caesar","Mary Queen of Scots","Chaka the Zulu","H.G Wells","Agatha Christie","Sir Isaac Newton","Philip Emeagwali",
                    "J.P Clark","Bishop Desmond Tutu","Jaja of Opobo", "Queen Amina of Zaria","Ousman Dan Fodio","Mansa Musa", "Cambyses the Great",
                    "Augustus Caesar","King David Ben Jesse", "King Solomon Ben David","Enid Blyton","William Shakespeare","Vladimir Putin",
                    "MKO Abiola","Gani Fawehinmi","Edson Arantes Dos Nascimento(Pele)","Rashidi Yekini","Tiger Woods","Yuri Gagarin","Neil Armstrong",
                    "Dennis Ritchie","James Gosling","Ken Thompson","Charles Dickens"
            };


static int ATOMIC_SERVE_RATE = 20;
static int MAX_ITEMS = 50;
static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);


static public JSONArray dataSource = new JSONArray();

static SecureRandom chooser = new SecureRandom();


static {
    Random r = new Random(System.currentTimeMillis());
    byte b[] = new byte[8];
    r.nextBytes(b);
    chooser = new SecureRandom(b);

    int genSize = chooser.nextInt(MAX_ITEMS);
    generate(genSize);


}



private static void generate(int genSize){
    synchronized (dataSource){




        List<String> tempSrc = new ArrayList<>();

        int sz = 0;
        while(genSize-- > 0){

            //if temp list is emptied, refill it.. Keep going till you have <code>genSize</code> items to serve.
            if(sz == 0){
                tempSrc = new ArrayList<>(Arrays.asList(ROOT_SOURCE));
                sz = tempSrc.size();
            }

            int index = chooser.nextInt(sz--);
            String item = tempSrc.remove(index);



            if(item != null && !item.isEmpty()){
                dataSource.put(item);
            }

        }
    }
}



public static String call(int offset){
    if(offset >= dataSource.length()){
        boolean choice = chooser.nextBoolean();
        if(choice) {
            generate(40 * ATOMIC_SERVE_RATE);
        }
    }


    JSONArray outputArray = new JSONArray();

    int i=offset;
    int count = ATOMIC_SERVE_RATE;
    while(count-- > 0){
     String item = dataSource.optString(i++);
     if(item != null && !item.isEmpty()) {
         outputArray.put(item);
     }


    }




return outputArray.toString();
}







}
