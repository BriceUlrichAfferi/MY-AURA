plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.aura"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }

  testOptions {
    unitTests.isReturnDefaultValues = true
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.annotation:annotation:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

      //ViewModel
  implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
  implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")

  //These go for 'by Viewmodel'
  implementation ("androidx.activity:activity-ktx:1.9.0")
  implementation ("androidx.fragment:fragment-ktx:1.8.0")

  // Retrofit dependencies
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-gson:2.11.0")

  //okhttp3 dependency

  implementation ("com.squareup.okhttp3:okhttp:4.11.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

  //DataStore dependency
  implementation ("androidx.datastore:datastore-preferences:1.1.1")
  implementation ("androidx.datastore:datastore-core:1.1.1")
  implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
  implementation("androidx.test.ext:junit-ktx:1.2.1")

  testImplementation("junit:junit:4.13.2")
  testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
  testImplementation ("io.mockk:mockk:1.12.0")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  testImplementation ("androidx.arch.core:core-testing:2.1.0")
  testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  testImplementation ("io.mockk:mockk:1.12.0")


  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  // Mockito dependencies

  testImplementation("org.mockito:mockito-inline:4.3.1")
  testImplementation("org.mockito:mockito-android:3.12.4")
  testImplementation ("org.mockito:mockito-core:3.12.4")

}