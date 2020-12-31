package com.example.sportdescriptionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText createViewSportName , createViewSportDescription;

    private ImageView imageView, createViewImageView;
    private Button saveInViewButton, addASportButton;
    private String textForTitle, textForDescription, getTheRealPath;
    private LinearLayout linearLayout;
    private int getTheTagForTheArrayList;
    private TextView forTheAddViewSportNameOne, forTheAddViewSportNameTwo, forTheAddViewSportsDecOne, forTheAddViewSportsDecTwo, theTennisView, theBaseBallView;
    boolean itIsTheFirstPart = true;
    boolean changedTheImageView = false;
    private Drawable d;
    // so we create the arraylists
    private ArrayList<String> theSportTitle, theSportDescription, theSportDrawable;

    // this is for the sake of the permission codes for obtaining images
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    // so here we will create a tag index to add in to the specific textview to provide a description for the revivision
    int tagNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // we set the basis for the permanent data
        loadData();

        linearLayout = (LinearLayout) findViewById(R.id.linlayout);
        addASportButton = (Button) findViewById(R.id.addASportButton);

        // so now we will set the textview's and inform the user that these are just example cases if clicked!
        theTennisView = (TextView) findViewById(R.id.theTennis);
        theBaseBallView = (TextView) findViewById(R.id.theBaseBall);

        theTennisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "This is just an example template and therefore cannot be edited", Toast.LENGTH_SHORT).show();
            }
        });

        theBaseBallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "This is just an example template and therefore cannot be edited", Toast.LENGTH_SHORT).show();
            }
        });



        // now we will check if the arraylist (any one) is empty
        // if they are not we will fill out the layout in line with what was placed before
        if (!theSportTitle.isEmpty()) {
            filloutTheDisplay();
        }


        addASportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so on the button click we open the function
                createTheDialog();
            }
        });

    }



    public void createTheDialog() {
        // so we create a dialog
        dialogBuilder = new AlertDialog.Builder(this);
        // so here we will get the view from the infopopup xml
        final View popUp = getLayoutInflater().inflate(R.layout.infopopup,null);
        // here we are initialising which can be done with the view
        createViewSportName = (EditText) popUp.findViewById(R.id.sportName);
        createViewSportDescription = (EditText) popUp.findViewById(R.id.sportDescription);
        saveInViewButton = (Button) popUp.findViewById(R.id.sportDetailsSave);
        createViewImageView = (ImageView) popUp.findViewById(R.id.sportImageView);


        // so here we set the view and allow the pop up to show
        dialogBuilder.setView(popUp);
        dialog = dialogBuilder.create();
        dialog.show();

        getTheImage(createViewImageView);

        createViewSportName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // so we get the text at the moment for the title
                textForTitle = createViewSportName.getText().toString();
            }
        });

        createViewSportDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // and we get it for the description
                textForDescription = createViewSportDescription.getText().toString();
            }
        });

        saveInViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so if there is text and an image we will save the details for future reference and dismiss the dialog
                if (textForTitle!=null && !TextUtils.isEmpty(textForTitle) && d!=null) {
                    // and here we will permanently save the title and if available the description
                    theSportTitle.add(textForTitle);
                    if (textForDescription == null) {
                        theSportDescription.add("");
                    } else {
                        theSportDescription.add(textForDescription);
                    }
                    // now we will pass in the path of theimage so as to be later on turned into a drawable
                    if (getTheRealPath != null) {
                        theSportDrawable.add(getTheRealPath);
                    } else {
                        theSportDrawable.add(d.toString());
                    }
                    // and this is were we permanently store the data
                    withSharedPreferences();
                    // and now we add the view and dismiss the dialog
                    addView();
                    dialog.dismiss();
                }
            }
        });
    }

    public void addView() {
        if (itIsTheFirstPart) {
            // so we will first set the view, initialise and add the view
            View addTheView = getLayoutInflater().inflate(R.layout.addmore,null,false);
            forTheAddViewSportNameOne = (TextView) addTheView.findViewById(R.id.theFirstTextView);
            forTheAddViewSportNameTwo = (TextView) addTheView.findViewById(R.id.theSecondTextView);
            // so we will be creating the textview's for the description so that we can later on add them to the views
            forTheAddViewSportsDecOne = (TextView) addTheView.findViewById(R.id.theInvisibleDescriptionOne);
            forTheAddViewSportsDecTwo = (TextView) addTheView.findViewById(R.id.theInvisibleDescriptionTwo);
            forTheAddViewSportNameOne.setBackground(d);
            // and here we set the text for the first one
            forTheAddViewSportsDecOne.setText(textForDescription);
            forTheAddViewSportNameOne.setText(textForTitle);
            // so we will set the tag to get a unique id for the desciption
            forTheAddViewSportNameOne.setTag(tagNumber);
            linearLayout.addView(addTheView);
            // so if itIsTheFirstPart is true we will set it to be false
            // and set the secondimageview as invisible so that the sports lists will show one at a time
            itIsTheFirstPart = false;
            forTheAddViewSportNameTwo.setVisibility(View.INVISIBLE);
            // and then we change the tag number so that it remains consistent with the arraylist of sportdescriptions
            tagNumber+=1;
        } else {
            // so when a second sport is added we will set the text and image as such and make it visible
            forTheAddViewSportNameTwo.setText(textForTitle);
            // so here we are setting the text for the second one
            forTheAddViewSportsDecTwo.setText(textForDescription);
            forTheAddViewSportNameTwo.setBackground(d);
            forTheAddViewSportNameTwo.setTag(tagNumber);
            forTheAddViewSportNameTwo.setVisibility(View.VISIBLE);
            // then we will set it to be true so that a new view is set on the next sport added
            itIsTheFirstPart = true;
            tagNumber+=1;
        }

        try {
            forTheAddViewSportNameOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviseView(view);
                }
            });

            forTheAddViewSportNameTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviseView(view);
                }
            });

        } catch (Exception e) {

        }

    }

    public Drawable getTheDrawable(String name) {
        // so we will set the file and drawable so that these will later on be added!
        Drawable thisDrawable;
        File file;
        try {
            // we set the name as per the parameter which will be obtained within the theSportDrawable array list
            file = new File(name);
            if (file.exists()) {
                // and if it exists we will set the drawable
                thisDrawable = Drawable.createFromPath(file.getAbsolutePath());
                changedTheImageView = true;
                return thisDrawable;
            }
        } catch (Exception e) {
            Toast.makeText(this, "There was an error here!!!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    public void filloutTheDisplay() {
        // so now we are going to iterate through an index so as to obtain the items of all the array lists
        int theIndexer = 0;
        while (theIndexer < theSportTitle.size()) {
            if (itIsTheFirstPart) {
                // so we will first set the view, initialise and add the view
                View addTheView = getLayoutInflater().inflate(R.layout.addmore,null,false);
                forTheAddViewSportNameOne = (TextView) addTheView.findViewById(R.id.theFirstTextView);
                forTheAddViewSportNameTwo = (TextView) addTheView.findViewById(R.id.theSecondTextView);
                // so we will be creating the textview's for the description so that we can later on add them to the views
                forTheAddViewSportsDecOne = (TextView) addTheView.findViewById(R.id.theInvisibleDescriptionOne);
                forTheAddViewSportsDecTwo = (TextView) addTheView.findViewById(R.id.theInvisibleDescriptionTwo);
                forTheAddViewSportNameOne.setTag(tagNumber);
                // now we will get the path for the drawable
                String j = theSportDrawable.get(theIndexer);
                // here we will set the drawable
                d = getTheDrawable(j);
                // and now we set the background
                forTheAddViewSportNameOne.setBackground(d);
                // so here we are obtaining the string as per the arraylist
                forTheAddViewSportNameOne.setText(theSportTitle.get(theIndexer));
                forTheAddViewSportsDecOne.setText(theSportDescription.get(theIndexer));
                linearLayout.addView(addTheView);
                // so if itIsTheFirstPart is true we will set it to be false
                // and set the secondimageview as invisible so that the sports lists will show one at a time
                itIsTheFirstPart = false;
                forTheAddViewSportNameTwo.setVisibility(View.INVISIBLE);
                tagNumber+=1;
            } else {
                // this will all follow the same principles as above
                forTheAddViewSportNameTwo.setText(theSportTitle.get(theIndexer));
                forTheAddViewSportsDecTwo.setText(theSportDescription.get(theIndexer));
                String j = theSportDrawable.get(theIndexer);
                d = getTheDrawable(j);
                forTheAddViewSportNameTwo.setBackground(d);
                forTheAddViewSportNameTwo.setTag(tagNumber);
                forTheAddViewSportNameTwo.setVisibility(View.VISIBLE);
                itIsTheFirstPart = true;
                tagNumber+=1;
            }

            try {
                forTheAddViewSportNameOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviseView(view);
                    }
                });

                forTheAddViewSportNameTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviseView(view);
                    }
                });

            } catch (Exception e) {

            }

            // and here we will add to the index each time
            theIndexer+=1;


        }

    }




    public void reviseView(View v) {
        // first we will make sure that the changedimageview is set to false for when changing our image view
        changedTheImageView = false;

        // so first we will get the particular text view's of the specific view chosen
        final TextView textView = (TextView) v.findViewById(R.id.theFirstTextView);
        final TextView textViewTwo = (TextView) v.findViewById(R.id.theSecondTextView);

        // now we will get the drawable of the specidic textview's
        Drawable b;


        // now we will open the dialog the dialog
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUp = getLayoutInflater().inflate(R.layout.infopopup,null);
        // now we will set the dialog
        createViewSportName = (EditText) popUp.findViewById(R.id.sportName);



        createViewSportDescription = (EditText) popUp.findViewById(R.id.sportDescription);
        try {
            // we will try set the title to the first text view
            createViewSportName.setText(textView.getText().toString());
            textForTitle = textView.getText().toString();
            // now we will get the drawable's of the first text view
            b = textView.getBackground();

            // now w e will get the tag of the sport name which will be conducive to the array list
            getTheTagForTheArrayList = (int) textView.getTag();
            if (!TextUtils.isEmpty(theSportDescription.get(getTheTagForTheArrayList))) {
                createViewSportDescription.setText(theSportDescription.get(getTheTagForTheArrayList));
            }

        } catch (Exception e) {
            // and if that does not work (because the view is the second one) we will set the text to the second text view
            createViewSportName.setText(textViewTwo.getText().toString());
            textForTitle = textViewTwo.getText().toString();
            // now we will get the drawable's of the second text view
            b = textViewTwo.getBackground();

            getTheTagForTheArrayList = (int) textViewTwo.getTag();
            // so now based on the tag we will get the description form the array list as per the index
            // so if the text is not empty we will set the sport description text to that in the array list
            if (!TextUtils.isEmpty(theSportDescription.get(getTheTagForTheArrayList))) {
                createViewSportDescription.setText(theSportDescription.get(getTheTagForTheArrayList));
            }
        }


        saveInViewButton = (Button) popUp.findViewById(R.id.sportDetailsSave);
        createViewImageView = (ImageView) popUp.findViewById(R.id.sportImageView);
        createViewImageView.setImageBitmap(null);
        createViewImageView.setBackground(b);

        // so here we set the view and allow the pop up to show
        dialogBuilder.setView(popUp);
        dialog = dialogBuilder.create();
        dialog.show();



        createViewSportName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // so we get the text at the moment for the title
                textForTitle = createViewSportName.getText().toString();
            }
        });

        createViewSportDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // and we get it for the description
                textForDescription = createViewSportDescription.getText().toString();
            }
        });

        getTheImage(createViewImageView);

        saveInViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so if there is text and an image we will save the details for future reference and dismiss the dialog
                if (textForTitle!=null && !TextUtils.isEmpty(textForTitle) && d!=null) {
                    try {
                        textView.setText(textForTitle);
                        // so here if there are any changes we will make sure that the arraylists are changed
                        theSportTitle.set(getTheTagForTheArrayList,textForTitle);
                        if (textForDescription!=null) {
                            theSportDescription.set(getTheTagForTheArrayList,textForDescription);
                        }
                        // so only if the image view has been changed do we change the background
                        // this is so that each text view background will not wind up being the last set drawable
                        if (changedTheImageView) {
                            textView.setBackground(d);
                            // so now we will set it back to false
                            changedTheImageView = false;
                            // and we will also replace the sport drawable if the image view was changed
                            theSportDrawable.set(getTheTagForTheArrayList,getTheRealPath);
                        } else {
                            Drawable c = textView.getBackground();
                            textView.setBackground(c);
                        }
                        withSharedPreferences();
                    } catch (Exception e) {
                        textViewTwo.setText(textForTitle);
                        theSportTitle.set(getTheTagForTheArrayList,textForTitle);
                        if (textForDescription!=null) {
                            theSportDescription.set(getTheTagForTheArrayList,textForDescription);
                        }
                        // explained above
                        if (changedTheImageView) {
                            textViewTwo.setBackground(d);
                            // and we set it back to false
                            changedTheImageView = false;
                            theSportDrawable.set(getTheTagForTheArrayList,getTheRealPath);
                        } else {
                            Drawable f = textViewTwo.getBackground();
                            textViewTwo.setBackground(f);
                        }
                        withSharedPreferences();
                    }
                    dialog.dismiss();
                }
            }
        });


    }



    public void getTheImage(ImageView iv) {

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so first we check the build and if it is less than marshmallow we will go about cheacking and asking for permissions
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // so if permission was not yet granted we will in turn request it
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        // so if permisson is granted we will pick up the image from the gallery
                        openGallery();
                    }
                } else {
                    // in the case of this sdk we can just immediately open the gallery
                    openGallery();
                }

            }
        });

    }

    public void openGallery() {

        // so we will first pick our image from the intent which is done through a special intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    // so this is to handle the event in the case that the permission was granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // so here we will be checking what the permission code is
        switch (requestCode) {
            case PERMISSION_CODE:{
                // so if the permission was granted we will open the gallery so as to pick up the stuff
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // so if the permisssion was in fact granted we will go about opening the gallery
                    openGallery();
                } else {
                    // and if permisssion id denied we will create a toast meessage
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


    // so now this will be to handle the event for the picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            File file;

            Uri uri = data.getData();
            if (getRealPathFromUri(uri) != null) {
                getTheRealPath  = getRealPathFromUri(uri);
                file = new File(getRealPathFromUri(uri));
            } else {
                file = new File("whatever");
                Toast.makeText(this, "We had an error here", Toast.LENGTH_LONG).show();
            }

            if (file.exists()) {
                d = Drawable.createFromPath(file.getAbsolutePath());
                // and now we will remove the original bacground of the app and set it to the new one as per the drawable

                createViewImageView.setImageBitmap(null);
                createViewImageView.setBackground(d);
                // so here we will be signifying that the image has been changed
                changedTheImageView = true;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // so here we will be getting the uri path of the item selected
    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri,null,null,null,null);

        cursor.moveToFirst();
        // so here  we will get the index path towards the image
        int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        // and we will return it as a string
        return cursor.getString(idx);
    }

    public void withSharedPreferences() {
        // so firest we create the sharedpreferences and the gson
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editorTwo = sharedPreferences.edit();
        SharedPreferences.Editor editorThree = sharedPreferences.edit();
        Gson gson = new Gson();
        // so now we will pass on our arraylist
        String json = gson.toJson(theSportTitle);
        String jsonTwo = gson.toJson(theSportDescription);
        String jsonThree = gson.toJson(theSportDrawable);
        editor.putString("sport names",json);
        editorTwo.putString("sport descriptions",jsonTwo);
        editorThree.putString("sports drawable",jsonThree);
        // so now we apply the constant
        editor.apply();
        editorTwo.apply();
        editorThree.apply();
    }

    public void loadData() {
        // we permanently store through shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // now we create a Gson  so as to later on convert it based on the type
        Gson gson = new Gson();
        String json = sharedPreferences.getString("sport names",null);
        String jsonTwo = sharedPreferences.getString("sport descriptions",null);
        String jsonThree = sharedPreferences.getString("sports drawable",null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Type typeTwo = new TypeToken<ArrayList<String>>() {}.getType();
        Type typeThree = new TypeToken<ArrayList<String>>() {}.getType();
        theSportTitle = gson.fromJson(json,type);
        theSportDescription = gson.fromJson(jsonTwo,typeTwo);
        theSportDrawable = gson.fromJson(jsonThree,typeThree);

        // and of course if the affirmations is null (when the app is first opened) we will initialise it
        if (theSportTitle == null) {
            theSportTitle = new ArrayList<>();
        }

        if (theSportDescription == null) {
            theSportDescription = new ArrayList<>();
        }

        if (theSportDrawable == null) {
            theSportDrawable = new ArrayList<>();
        }

    }


}