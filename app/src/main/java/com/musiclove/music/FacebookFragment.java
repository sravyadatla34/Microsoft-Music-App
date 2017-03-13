package com.musiclove.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.musiclove.music.Model.FriendsDB;
import com.musiclove.music.Model.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;


public class FacebookFragment extends Fragment{

    DatabaseHelper myDb;
    private LoginButton loginButton;
    private MobileServiceClient mClient;
    private Button getLibrary;
    private Button getShared;
    public ArrayList<String> friends;
    private boolean postingEnabled = false;
    public int inProgress = 0;
    private static final String PERMISSION = "publish_actions";
    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.example.hellofacebook:PendingAction";

    //private Button postStatusUpdateButton;
   // private Button postPhotoButton;
    private ImageView profilePicImageView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;

    private boolean canPresentShareDialogWithPhotos;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    public static void waiting (int n){

        long t0, t1;

        t0 = System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while (t1 - t0 < n);
    }
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("FacebookFragment", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("FacebookFragment", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("FacebookFragment", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(getActivity());
        try {
            mClient = new MobileServiceClient(
                    "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
                    getActivity()
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        FacebookSdk.sdkInitialize(getActivity());

        // Other app specific specialization
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_facebook, parent, false);
        loginButton = (LoginButton) v.findViewById(R.id.loginButton);
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast toast = Toast.makeText(getActivity(), "Logged In", Toast.LENGTH_SHORT);

                myDb.deleteTable("user_table");
                myDb.deleteTable("song_table");
                myDb.deleteTable("friends_table");
                postingEnabled = true;
                 getLibrary.setVisibility(View.VISIBLE);
                getShared.setVisibility(View.VISIBLE);
                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        loginResult.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {

                            public void onCompleted(GraphResponse response) {
                                try {
                                    Profile profile = Profile.getCurrentProfile();
                                    SharedPreferences settings = getActivity().getSharedPreferences("mysettings",
                                            Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor = settings.edit();

                                    editor.putString("uid", profile.getId());
                                    editor.putString("name",profile.getName());
                                    editor.putString("pic",profile.getProfilePictureUri(200,200).toString());
                                    editor.commit();
                                    User item = new User();
                                    item.setUid(profile.getId());
                                    item.setUname(profile.getName());
                                    item.setPic_url(profile.getProfilePictureUri(200,200).toString());
                                    mClient.getTable(User.class).insert(item, new TableOperationCallback<User>() {
                                        public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
                                            if (exception == null) {
                                                // Insert succeeded
                                            } else {
                                                // Insert failed
                                            }
                                        }
                                    });


                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    String jsondata  = rawName.toString();
                                    JSONArray friendslist;
                                    friends = new ArrayList<String>();
                                    friendslist = new JSONArray(jsondata);

                                    FriendsDB frnd ;
                                    String usid = profile.getId();
                                    for (int l=0; l < friendslist.length(); l++) {
                                        friends.add(friendslist.getJSONObject(l).getString("id"));
                                        System.out.println("HELLO FRIENDS :"+friendslist.getJSONObject(l).getString("name"));
                                        System.out.println("HELLO FRIENDS ID :"+friendslist.getJSONObject(l).getString("id"));
                                        System.out.println("HELLO FRIENDS TYPE ID :"+friendslist.getJSONObject(l).getString("id").getClass().getName());
                                        System.out.println("HELLO USER:"+usid);
                                        frnd = new FriendsDB(usid,friendslist.getJSONObject(l).getString("id"));
                                        mClient.getTable(FriendsDB.class).insert(frnd, new TableOperationCallback<FriendsDB>() {
                                            public void onCompleted(FriendsDB entity, Exception exception, ServiceFilterResponse response) {
                                                if (exception == null) {
                                                    // Insert succeeded
                                                } else {
                                                    // Insert failed
                                                }
                                            }
                                        });

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
                toast.show();
                handlePendingAction();
                updateUI(friends);
            }

            @Override
            public void onCancel() {
                // App code
                if (pendingAction != PendingAction.NONE) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                updateUI(friends);
            }

            @Override
            public void onError(FacebookException exception) {
                if (pendingAction != PendingAction.NONE
                        && exception instanceof FacebookAuthorizationException) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                updateUI(friends);

            }

            private void showAlert() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.cancelled)
                        .setMessage(R.string.permission_not_granted)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }

        });
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI(friends);
                handlePendingAction();
            }
        };


    profilePicImageView = (ImageView) v.findViewById(R.id.profilePicture);
    greeting = (TextView) v.findViewById(R.id.greeting);

        //postStatusUpdateButton = (Button) v.findViewById(R.id.postStatusUpdateButton);
        /*postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });*/

        /*postPhotoButton = (Button) v.findViewById(R.id.postPhotoButton);
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostPhoto();
            }
        });*/

        getLibrary = (Button) v.findViewById(R.id.getLibraryButton);
        getShared = (Button)v.findViewById(R.id.getSharedButton);
        getShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SongPostList.class));

            }
        });

        getLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });


        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);


    loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            if(!postingEnabled) {

                postingEnabled = true;
          //      postPhotoButton.setVisibility(View.VISIBLE);
            //    postStatusUpdateButton.setVisibility(View.VISIBLE);
                getLibrary.setVisibility(View.VISIBLE);
                getShared.setVisibility(View.VISIBLE);
            }else{

                postingEnabled = false;
               // postPhotoButton.setVisibility(View.GONE);
                //postStatusUpdateButton.setVisibility(View.GONE);
                getLibrary.setVisibility(View.GONE);
                getShared.setVisibility(View.GONE);

            }

//                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//                                if (object != null) {
//                                    Log.d("Me Request",object.toString());
//                                    Toast t = Toast.makeText(getActivity(), object.toString(), Toast.LENGTH_SHORT);
//                                    t.show();
//                                }
//
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,link,email");
//                request.setParameters(parameters);
//                request.executeAsync();
        }
    });


//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "user_friends"));
//
//
//
//            }
//        });
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(getActivity());

        updateUI(friends);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }



    private void updateUI(ArrayList<String> frnds) {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        //postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
        //postPhotoButton.setEnabled(enableButtons || canPresentShareDialogWithPhotos);

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            new LoadProfileImage(profilePicImageView).execute(profile.getProfilePictureUri(200, 200).toString());
            greeting.setText(getString(R.string.hello_user, profile.getName()));

            System.out.println("FRND:"+friends);

            postingEnabled = true;
            //postPhotoButton.setVisibility(View.VISIBLE);
            //postStatusUpdateButton.setVisibility(View.VISIBLE);
            getLibrary.setVisibility(View.VISIBLE);
            getShared.setVisibility(View.VISIBLE);
        } else {
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.user_default);
            profilePicImageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(), icon, 200, 200, 200, false, false, false, false));
            greeting.setText(null);
            postingEnabled = false;
           // postPhotoButton.setVisibility(View.GONE);
            //postStatusUpdateButton.setVisibility(View.GONE);
            getLibrary.setVisibility(View.GONE);
            getShared.setVisibility(View.GONE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Integrate Facebook Login to your Android App")
                .setContentDescription(
                        "This app shows how to integrate Facebook Login to your Android App")
                .setContentUrl(Uri.parse("http://www.androidtutorialpoint.com/material-design/adding-facebook-login-to-android-app/"))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }

    private void postPhoto() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.androidlogo);
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().setPhotos(photos).build();
        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
            // We need to get new permissions, then complete the action when we get called back.
            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    Arrays.asList(PERMISSION));
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result != null) {


                Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(),resized,250,200,200, false, false, false, false));

            }
        }
    }


}





