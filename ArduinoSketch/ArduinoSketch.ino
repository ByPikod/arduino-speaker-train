#include <SD.h> // SD card library | SD kart kütüphanesi
#include <SPI.h> // SD card library | SD kart kütüphanesi
#include <TMRpcm.h> // The library of play music readed from SD | SD'den okunan müzik dosyasını oynatma kütüphanesi
#include <MemoryFree.h>

#define SD_Pin 10 // SD pin | SD pin'i

TMRpcm tmrpcm; // Creatimng tmrPCM class | tmrPCM sınıfını oluşturuyoruz.

// To use functions and classes everywhere we have to define them on the top of the project
// Fonksiyonlarımızı ve sınıflarımızı her yerde kullanabilmek için projenin en üstünde tanımlıyoruz.
void readLoop();
void sendByte(byte data);
void sendString(String data);
void onByte(int data);
void onString(String data);
void sendFile();

// Setup | Kurulum
void setup(){

  tmrpcm.speakerPin = 9; // This is the pin to output audio - Ses çıkışı alacağımız pin.

  Serial.begin(9600);

  pinMode(SD_Pin, OUTPUT); // SD Pin
  digitalWrite(SD_Pin, HIGH);

  // Motor pins | Motor pinleri
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  
  // We set the pins low as default | Varsayılan olarak pinleri güç kapalı durumuna çektik.
  digitalWrite(7, LOW);
  digitalWrite(8, LOW);
  
  // Prepare file names to transfer phone.
  
  //getFiles();
  
}

bool readString = false;
String readed = "";

void loop(){ 
  
  readLoop();
  delay(1); 
  
}


void sendFiles(){

  tmrpcm.pause();
  
  Serial.write((byte) 253); // Beggining of the list | Listenin başı
  sendString(String("ssl")); // ssl -> sending sound list
  
  File dir = SD.open("/");
  while(true){
    
    File entry = dir.openNextFile();
    if(!entry) break;
    
    if(entry.isDirectory()){
      entry.close();
      continue; 
    }
    
    sendString(entry.name());
    entry.close();
    
  }

  dir.close();
  Serial.write((byte) 252); // End of the list | Listenin sonu
  tmrpcm.pause();

}

void readLoop(){

  if( !Serial.available() ) return;

  int b = Serial.read();
  
  if(readString){
   
    if(b == 254) {
      onString(readed);
      readed = "";
      readString = false;
      return;

    }

    readed = readed + ((char) b);
    
    return;
  }

  if(b == 255) readString = true; else onByte(b);
  
}

void sendByte(byte data){
  Serial.write(data);
}

void sendString(String data){

  int len = data.length() +1;
  byte chars[len];
  data.getBytes(chars, len);
  
  Serial.write(255);
  for(int i = 0; i < len-1; i++){
    Serial.write(chars[i]);
  }

  // End of the string | Metnin sonu
  Serial.write(254);
  
}

/* Events */

void onByte(int data){
  switch(data){
    case 1:
      digitalWrite(7, HIGH);
      digitalWrite(8, LOW);
      break;
    case 2:
      digitalWrite(7, LOW);
      digitalWrite(8, HIGH);
      break;
    case 3:
      digitalWrite(7, HIGH);
      digitalWrite(8, HIGH);
      delay(500);
      digitalWrite(7, LOW);
      digitalWrite(8, LOW);
      break;
    case 4:
      sendFiles();
      break;
  }
}

void onString(String data){
  if(data.startsWith("play")){
    String mName = data.substring(5);
    tmrpcm.play(mName.c_str());
  }else
  if(data == "pause"){
      tmrpcm.pause();
      digitalWrite(9, LOW);
  }
}
