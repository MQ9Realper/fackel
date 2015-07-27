package dennismwebia.com.fackel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	private Camera camera;
	private boolean isFlashOn;
	private boolean hasFlash;
	Camera.Parameters params;
	private ProgressDialog progressDialog;
	private ProgressDialog progressDialog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		TextView textFlashLight = (TextView) findViewById(R.id.text_flashlight);
		AssetManager assetManager = getAssets();
		Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/RobotoRegular.ttf");
		textFlashLight.setTypeface(typeface);

		final Button button = (Button) findViewById(R.id.button);

		Switch aSwitch = (Switch) findViewById(R.id.switch1);
		aSwitch.setChecked(false);
		aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					turnOnFlashlight();
					button.setText(R.string.flashlight_on);
				} else {
					turnOffFlashlight();
					button.setText(R.string.flashlight_off);
				}

			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		turnOffFlash();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if (hasFlash)
			turnOnFlash();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// on starting the app get the camera params
		getCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// on stop release the camera
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	public void turnOffFlashlight() {
		AsyncTask<Void, Void, Void> flashOffTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				progressDialog2 = new ProgressDialog(MainActivity.this);
				progressDialog2.setTitle("Fackel");
				progressDialog2.setMessage("Turning off flashlight.....");
				progressDialog2.setCancelable(false);
				progressDialog2.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				turnOffFlash();
				return null;
			}

			@Override
			protected void onPostExecute(Void results) {
				progressDialog2.dismiss();
			}
		};
		flashOffTask.execute((Void[]) null);
	}


	public void turnOnFlashlight() {
		AsyncTask<Void, Void, Void> flashTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				if (!hasFlash) {
					// Show alert message and close the application
					AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
							.create();
					alert.setTitle("Fackel");
					alert.setMessage("Sorry, your device doesn't support flash light!");
					alert.setButton(1, "OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					alert.show();
				} else {
					progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setTitle("Fackel Flashlight");
					progressDialog.setMessage("Turning on flashlight....");
					progressDialog.setCancelable(false);
					progressDialog.show();
				}

			}

			@Override
			protected Void doInBackground(Void... params) {
				getCamera();
				turnOnFlash();
				return null;
			}

			@Override
			protected void onPostExecute(Void results) {
				progressDialog.dismiss();

			}
		};
		flashTask.execute((Void[]) null);

	}

	// Get the camera
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Error: ", e.getMessage());
			}
		}
	}

	// Turning On flash
	private void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;

		}

	}

	// Turning Off flash
	private void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;

		}
	}
}
