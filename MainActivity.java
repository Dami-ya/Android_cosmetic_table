package cosmetic.com.cosmetictable_server;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int check = 0;
    TextView msg[] = new TextView[15];
    ImageView imageView[] = new ImageView[15];
    String bodyfantasy_url = "https://firebasestorage.googleapis.com/v0/b/cosmetictable01.appspot.com/o/cosmetictable_image%2F03_bodyfanta.png?alt=media&token=71bb3886-7cc2-4d50-a479-d12094bab096";
    String threece_url = "https://firebasestorage.googleapis.com/v0/b/cosmetictable01.appspot.com/o/cosmetictable_image%2F03_bodyfanta.png?alt=media&token=71bb3886-7cc2-4d50-a479-d12094bab096";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msg[0] = (TextView) findViewById(R.id.msg0);
        msg[1] = (TextView) findViewById(R.id.msg1);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg[0].getVisibility() == View.VISIBLE) {
                    msg[0].setVisibility(View.GONE);
                } else {
                    msg[0].setVisibility(View.VISIBLE);
                }
                if (msg[1].getVisibility() == View.VISIBLE) {
                    msg[1].setVisibility(View.GONE);
                } else {
                    msg[1].setVisibility(View.VISIBLE);
                }
            }
        };
        ImageView iv1 = (ImageView) findViewById(R.id.iv1);
        iv1.setOnClickListener(ocl);
        ImageView iv2 = (ImageView) findViewById(R.id.iv2);
        iv2.setOnClickListener(ocl);

        ImageButton search_button = (ImageButton) findViewById(R.id.search);
        //search아이콘은 Button이 아니라 ImageButton타입

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);

            }
        });


        // url 받아오기 
        Intent intent = new Intent(getIntent());//인텐트  받아오고
        final String image_url = intent.getStringExtra("image_url");//인텐트로 부터 데이터 가져오고
        Toast t = Toast.makeText(this, "", Toast.LENGTH_SHORT);//값 사용 예시 ex.토스트
        t.show();


        //1, 2번쨰 아이템 임의로 받아오기
        final Bitmap[] bitmap = new Bitmap[15];

        //////1번
        imageView[0] = (ImageView)findViewById(R.id.iv1);
        new Thread(new Runnable() {
            public void run() {
                try {
                    bitmap[0] = getBitmap(bodyfantasy_url);
                } catch (Exception e) {

                } finally {
                    if (bitmap[0] != null) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("NewApi")
                            public void run() {
                                imageView[0].setImageBitmap(bitmap[0]);
                            }
                        });
                    }
                }
            }
        }).start();


        //////2번
        imageView[1] = (ImageView)findViewById(R.id.iv2);
        new Thread(new Runnable() {
            public void run() {
                try {
                    bitmap[1] = getBitmap("https://firebasestorage.googleapis.com/v0/b/cosmetictable01.appspot.com/o/cosmetictable_image%2F07_3ce.png?alt=media&token=92834d1d-ba63-4c59-a539-8876ee7e6d2c");
                } catch (Exception e) {

                } finally {
                    if (bitmap[1] != null) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("NewApi")
                            public void run() {
                                imageView[1].setImageBitmap(bitmap[1]);
                            }
                        });
                    }
                }
            }
        }).start();


        imageView[2] = (ImageView)findViewById(R.id.iv3);
        imageView[3] = (ImageView)findViewById(R.id.iv4);


        new Thread(new Runnable() {
            public void run() {
                try {
                    bitmap[2] = getBitmap(image_url);
                } catch (Exception e) {

                } finally {
                    if (bitmap[2] != null) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("NewApi")
                            public void run() {
                                imageView[2].setImageBitmap(bitmap[2]);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * image url을 받아서 bitmap을 생성하고 리턴합니다
     *
     * @param url 얻고자 하는 image url
     * @return 생성된 bitmap
     */
    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try {
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }
}
