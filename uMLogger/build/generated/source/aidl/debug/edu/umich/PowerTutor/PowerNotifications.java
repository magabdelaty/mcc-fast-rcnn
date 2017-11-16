/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\git\\MccObjectDetection\\uMLogger\\src\\main\\aidl\\edu\\umich\\PowerTutor\\PowerNotifications.aidl
 */
package edu.umich.PowerTutor;
public interface PowerNotifications extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.umich.PowerTutor.PowerNotifications
{
private static final java.lang.String DESCRIPTOR = "edu.umich.PowerTutor.PowerNotifications";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.umich.PowerTutor.PowerNotifications interface,
 * generating a proxy if needed.
 */
public static edu.umich.PowerTutor.PowerNotifications asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.umich.PowerTutor.PowerNotifications))) {
return ((edu.umich.PowerTutor.PowerNotifications)iin);
}
return new edu.umich.PowerTutor.PowerNotifications.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_noteStartWakelock:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
this.noteStartWakelock(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStopWakelock:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
this.noteStopWakelock(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStartSensor:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.noteStartSensor(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStopSensor:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.noteStopSensor(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStartGps:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteStartGps(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStopGps:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteStopGps(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteScreenBrightness:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteScreenBrightness(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStartMedia:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.noteStartMedia(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_noteStopMedia:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.noteStopMedia(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_noteVideoSize:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.noteVideoSize(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_noteSystemMediaCall:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteSystemMediaCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteScreenOn:
{
data.enforceInterface(DESCRIPTOR);
this.noteScreenOn();
reply.writeNoException();
return true;
}
case TRANSACTION_noteScreenOff:
{
data.enforceInterface(DESCRIPTOR);
this.noteScreenOff();
reply.writeNoException();
return true;
}
case TRANSACTION_noteInputEvent:
{
data.enforceInterface(DESCRIPTOR);
this.noteInputEvent();
reply.writeNoException();
return true;
}
case TRANSACTION_noteUserActivity:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.noteUserActivity(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_notePhoneOn:
{
data.enforceInterface(DESCRIPTOR);
this.notePhoneOn();
reply.writeNoException();
return true;
}
case TRANSACTION_notePhoneOff:
{
data.enforceInterface(DESCRIPTOR);
this.notePhoneOff();
reply.writeNoException();
return true;
}
case TRANSACTION_notePhoneDataConnectionState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.notePhoneDataConnectionState(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiOn:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteWifiOn(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiOff:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteWifiOff(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiRunning:
{
data.enforceInterface(DESCRIPTOR);
this.noteWifiRunning();
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiStopped:
{
data.enforceInterface(DESCRIPTOR);
this.noteWifiStopped();
reply.writeNoException();
return true;
}
case TRANSACTION_noteBluetoothOn:
{
data.enforceInterface(DESCRIPTOR);
this.noteBluetoothOn();
reply.writeNoException();
return true;
}
case TRANSACTION_noteBluetoothOff:
{
data.enforceInterface(DESCRIPTOR);
this.noteBluetoothOff();
reply.writeNoException();
return true;
}
case TRANSACTION_noteFullWifiLockAcquired:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteFullWifiLockAcquired(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteFullWifiLockReleased:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteFullWifiLockReleased(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteScanWifiLockAcquired:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteScanWifiLockAcquired(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteScanWifiLockReleased:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteScanWifiLockReleased(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiMulticastEnabled:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteWifiMulticastEnabled(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteWifiMulticastDisabled:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteWifiMulticastDisabled(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setOnBattery:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
this.setOnBattery(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_recordCurrentLevel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.recordCurrentLevel(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteVideoOn:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteVideoOn(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteVideoOff:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteVideoOff(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteAudioOn:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteAudioOn(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_noteAudioOff:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.noteAudioOff(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.umich.PowerTutor.PowerNotifications
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// These are the notifications that are actually supported.  The rest have
// potential to be supported but aren't needed at the moment so aren't
// actually hooked.

@Override public void noteStartWakelock(int uid, java.lang.String name, int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(name);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_noteStartWakelock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStopWakelock(int uid, java.lang.String name, int type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(name);
_data.writeInt(type);
mRemote.transact(Stub.TRANSACTION_noteStopWakelock, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStartSensor(int uid, int sensor) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(sensor);
mRemote.transact(Stub.TRANSACTION_noteStartSensor, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStopSensor(int uid, int sensor) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(sensor);
mRemote.transact(Stub.TRANSACTION_noteStopSensor, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStartGps(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteStartGps, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStopGps(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteStopGps, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteScreenBrightness(int brightness) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(brightness);
mRemote.transact(Stub.TRANSACTION_noteScreenBrightness, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStartMedia(int uid, int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_noteStartMedia, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteStopMedia(int uid, int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_noteStopMedia, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteVideoSize(int uid, int id, int width, int height) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(id);
_data.writeInt(width);
_data.writeInt(height);
mRemote.transact(Stub.TRANSACTION_noteVideoSize, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteSystemMediaCall(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteSystemMediaCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteScreenOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteScreenOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteScreenOff() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteScreenOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteInputEvent() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteInputEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteUserActivity(int uid, int event) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(event);
mRemote.transact(Stub.TRANSACTION_noteUserActivity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notePhoneOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notePhoneOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notePhoneOff() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notePhoneOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void notePhoneDataConnectionState(int dataType, boolean hasData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(dataType);
_data.writeInt(((hasData)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_notePhoneDataConnectionState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiOn(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteWifiOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiOff(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteWifiOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteWifiRunning, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiStopped() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteWifiStopped, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteBluetoothOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteBluetoothOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteBluetoothOff() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_noteBluetoothOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteFullWifiLockAcquired(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteFullWifiLockAcquired, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteFullWifiLockReleased(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteFullWifiLockReleased, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteScanWifiLockAcquired(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteScanWifiLockAcquired, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteScanWifiLockReleased(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteScanWifiLockReleased, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiMulticastEnabled(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteWifiMulticastEnabled, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteWifiMulticastDisabled(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteWifiMulticastDisabled, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setOnBattery(boolean onBattery, int level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((onBattery)?(1):(0)));
_data.writeInt(level);
mRemote.transact(Stub.TRANSACTION_setOnBattery, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void recordCurrentLevel(int level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(level);
mRemote.transact(Stub.TRANSACTION_recordCurrentLevel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* Also got rid of the non-notification calls.
     * byte[] getStatistics();
     * long getAwakeTimeBattery();
     * long getAwakeTimePlugged();
     *//* Added functions to the normal IBatteryStats interface. */
@Override public void noteVideoOn(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteVideoOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteVideoOff(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteVideoOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteAudioOn(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteAudioOn, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void noteAudioOff(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_noteAudioOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_noteStartWakelock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_noteStopWakelock = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_noteStartSensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_noteStopSensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_noteStartGps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_noteStopGps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_noteScreenBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_noteStartMedia = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_noteStopMedia = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_noteVideoSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_noteSystemMediaCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_noteScreenOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_noteScreenOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_noteInputEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_noteUserActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_notePhoneOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_notePhoneOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_notePhoneDataConnectionState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_noteWifiOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_noteWifiOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_noteWifiRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_noteWifiStopped = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_noteBluetoothOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_noteBluetoothOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_noteFullWifiLockAcquired = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_noteFullWifiLockReleased = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_noteScanWifiLockAcquired = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_noteScanWifiLockReleased = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_noteWifiMulticastEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_noteWifiMulticastDisabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_setOnBattery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_recordCurrentLevel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_noteVideoOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_noteVideoOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_noteAudioOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_noteAudioOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
}
// These are the notifications that are actually supported.  The rest have
// potential to be supported but aren't needed at the moment so aren't
// actually hooked.

public void noteStartWakelock(int uid, java.lang.String name, int type) throws android.os.RemoteException;
public void noteStopWakelock(int uid, java.lang.String name, int type) throws android.os.RemoteException;
public void noteStartSensor(int uid, int sensor) throws android.os.RemoteException;
public void noteStopSensor(int uid, int sensor) throws android.os.RemoteException;
public void noteStartGps(int uid) throws android.os.RemoteException;
public void noteStopGps(int uid) throws android.os.RemoteException;
public void noteScreenBrightness(int brightness) throws android.os.RemoteException;
public void noteStartMedia(int uid, int id) throws android.os.RemoteException;
public void noteStopMedia(int uid, int id) throws android.os.RemoteException;
public void noteVideoSize(int uid, int id, int width, int height) throws android.os.RemoteException;
public void noteSystemMediaCall(int uid) throws android.os.RemoteException;
public void noteScreenOn() throws android.os.RemoteException;
public void noteScreenOff() throws android.os.RemoteException;
public void noteInputEvent() throws android.os.RemoteException;
public void noteUserActivity(int uid, int event) throws android.os.RemoteException;
public void notePhoneOn() throws android.os.RemoteException;
public void notePhoneOff() throws android.os.RemoteException;
public void notePhoneDataConnectionState(int dataType, boolean hasData) throws android.os.RemoteException;
public void noteWifiOn(int uid) throws android.os.RemoteException;
public void noteWifiOff(int uid) throws android.os.RemoteException;
public void noteWifiRunning() throws android.os.RemoteException;
public void noteWifiStopped() throws android.os.RemoteException;
public void noteBluetoothOn() throws android.os.RemoteException;
public void noteBluetoothOff() throws android.os.RemoteException;
public void noteFullWifiLockAcquired(int uid) throws android.os.RemoteException;
public void noteFullWifiLockReleased(int uid) throws android.os.RemoteException;
public void noteScanWifiLockAcquired(int uid) throws android.os.RemoteException;
public void noteScanWifiLockReleased(int uid) throws android.os.RemoteException;
public void noteWifiMulticastEnabled(int uid) throws android.os.RemoteException;
public void noteWifiMulticastDisabled(int uid) throws android.os.RemoteException;
public void setOnBattery(boolean onBattery, int level) throws android.os.RemoteException;
public void recordCurrentLevel(int level) throws android.os.RemoteException;
/* Also got rid of the non-notification calls.
     * byte[] getStatistics();
     * long getAwakeTimeBattery();
     * long getAwakeTimePlugged();
     *//* Added functions to the normal IBatteryStats interface. */
public void noteVideoOn(int uid) throws android.os.RemoteException;
public void noteVideoOff(int uid) throws android.os.RemoteException;
public void noteAudioOn(int uid) throws android.os.RemoteException;
public void noteAudioOff(int uid) throws android.os.RemoteException;
}
