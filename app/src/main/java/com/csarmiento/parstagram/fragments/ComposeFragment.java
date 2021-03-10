package com.csarmiento.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.csarmiento.parstagram.LoginActivity;
import com.csarmiento.parstagram.MainActivity;
import com.csarmiento.parstagram.Post;
import com.csarmiento.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

  public static final String TAG = "ComposeFragment";
  public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
  public String photoFileName = "Photo.jpg";

  private EditText etDescription;
  private Button btnCaptureImage;
  private ImageView ivPostImage;
  private Button btnSubmit;
  private File photoFile;
  private Button btnLogout;

  public ComposeFragment() {
    // Required empty public constructor
  }

//  public static ComposeFragment newInstance() {
//    ComposeFragment fragment = new ComposeFragment();
//    Bundle args = new Bundle();
//    return fragment;
//  }

//  @Override
//  public void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_compose, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    etDescription = view.findViewById(R.id.etDescription);
    btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
    ivPostImage = view.findViewById(R.id.ivPostImage);
    btnSubmit = view.findViewById(R.id.btnSubmit);
    btnLogout = view.findViewById(R.id.btnLogout);

    btnCaptureImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launchCamera();
      }
    });

    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String description = etDescription.getText().toString();
        if (description.isEmpty()) {
          Toast.makeText(getContext(), "Description cannot be empty." , Toast.LENGTH_SHORT).show();
          return;
        }

        if (photoFile == null || ivPostImage.getDrawable() == null) {
          Toast.makeText(getContext( ), "There is no image!", Toast.LENGTH_SHORT).show();
          return;
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(description, currentUser, photoFile);
      }
    });

    btnLogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        logout();
      }
    });
  }

  private void logout() {
    ParseUser.logOut();
    if (ParseUser.getCurrentUser() == null ) {
      Intent i = new Intent(getContext(), LoginActivity.class);
      startActivity(i);
    }
  }

  private void launchCamera() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    photoFile = getPhotoFileUri(photoFileName);

    Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        // by this point we have the camera photo on disk
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        ivPostImage.setImageBitmap(takenImage);
      } else { // Result was a failure
        Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public File getPhotoFileUri(String fileName) {
    File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
      Log.d(TAG, "failed to create directory");
    }

    return new File(mediaStorageDir.getPath() + File.separator + fileName);
  }

  private void savePost(String description, ParseUser currentUser, File photoFile) {
    Post post = new Post();
    post.setDescription(description);
    post.setImage(new ParseFile(photoFile));
    post.setUser(currentUser);
    post.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "Error while saving post", e);
          Toast.makeText(getContext(), "Error while saving post." , Toast.LENGTH_SHORT).show();
          return;
        }
        Log.i(TAG, "Post was successfully saved.");
        etDescription.setText("");
        ivPostImage.setImageResource(0);

      }
    });
  }
}