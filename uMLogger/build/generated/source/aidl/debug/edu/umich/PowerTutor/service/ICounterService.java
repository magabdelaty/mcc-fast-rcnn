/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\git\\MccObjectDetection\\uMLogger\\src\\main\\aidl\\edu\\umich\\PowerTutor\\service\\ICounterService.aidl
 */
package edu.umich.PowerTutor.service;
public interface ICounterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.umich.PowerTutor.service.ICounterService
{
private static final java.lang.String DESCRIPTOR = "edu.umich.PowerTutor.service.ICounterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.umich.PowerTutor.service.ICounterService interface,
 * generating a proxy if needed.
 */
public static edu.umich.PowerTutor.service.ICounterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.umich.PowerTutor.service.ICounterService))) {
return ((edu.umich.PowerTutor.service.ICounterService)iin);
}
return new edu.umich.PowerTutor.service.ICounterService.Stub.Proxy(obj);
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
case TRANSACTION_getComponents:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getComponents();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_getComponentsMaxPower:
{
data.enforceInterface(DESCRIPTOR);
int[] _result = this.getComponentsMaxPower();
reply.writeNoException();
reply.writeIntArray(_result);
return true;
}
case TRANSACTION_getNoUidMask:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getNoUidMask();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getComponentHistory:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int[] _result = this.getComponentHistory(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeIntArray(_result);
return true;
}
case TRANSACTION_getTotals:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
long[] _result = this.getTotals(_arg0, _arg1);
reply.writeNoException();
reply.writeLongArray(_result);
return true;
}
case TRANSACTION_getMeans:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
long[] _result = this.getMeans(_arg0, _arg1);
reply.writeNoException();
reply.writeLongArray(_result);
return true;
}
case TRANSACTION_getRuntime:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
long _result = this.getRuntime(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getUidInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte[] _result = this.getUidInfo(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
case TRANSACTION_getUidExtra:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
long _result = this.getUidExtra(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.umich.PowerTutor.service.ICounterService
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
@Override public java.lang.String[] getComponents() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getComponents, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int[] getComponentsMaxPower() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getComponentsMaxPower, _data, _reply, 0);
_reply.readException();
_result = _reply.createIntArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getNoUidMask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNoUidMask, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int[] getComponentHistory(int count, int componentId, int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
_data.writeInt(componentId);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_getComponentHistory, _data, _reply, 0);
_reply.readException();
_result = _reply.createIntArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Returns the total energy consumption for each component in the same order
// that the components were returned in getComponents() and in the same order
// that the components are populated by PhoneSelector.generateComponents().
//
// uid can be specified to make the information specific to a single user
// id or SystemInfo.AID_ALL can be specified to request global information.
//
// windowType should be one of Counter.WINDOW_MINUTE, Counter.WINDOW_HOUR,
// Counter.WINDOW_DAY, Counter.WINDOW_TOTAL to request the window that the
// energy usage will be calculated over.
//
// The returned result is given in mJ.

@Override public long[] getTotals(int uid, int windowType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(windowType);
mRemote.transact(Stub.TRANSACTION_getTotals, _data, _reply, 0);
_reply.readException();
_result = _reply.createLongArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Like getTotals() except that each entry is divided by how long the given
// uid was running.  If SystemInfo.AID_ALL is provided this is effectively
// like dividing each entry by the window size. (unless PowerTutor hasn't
// been running that long).

@Override public long[] getMeans(int uid, int windowType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(windowType);
mRemote.transact(Stub.TRANSACTION_getMeans, _data, _reply, 0);
_reply.readException();
_result = _reply.createLongArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Gets the total time that this uid has been running in seconds.

@Override public long getRuntime(int uid, int windowType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(windowType);
mRemote.transact(Stub.TRANSACTION_getRuntime, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Returns a byte array representing a serialized array of UidInfo structures.
// See UidInfo.java for what information is given.  Note that members marked
// as transient are not filled in.
// Power contributions from component i will be dropped if the ith bit is set
// in ignoreMask.  Providing 0 for ignoreMask will give results for all
// components.
//
// Example Usage:
//   byte[] rawUidInfo = counterService.getUidInfo(windowType);
//   UidInfo[] uidInfos = (UidInfo[])new ObjectInputStream(
//       new ByteArrayInputStream(rawUidInfo)).readObject();

@Override public byte[] getUidInfo(int windowType, int ignoreMask) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(windowType);
_data.writeInt(ignoreMask);
mRemote.transact(Stub.TRANSACTION_getUidInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Return miscellaneous data point for the passed uid.
// Current extras included:
//   OLEDSCORE

@Override public long getUidExtra(java.lang.String name, int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_getUidExtra, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getComponents = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getComponentsMaxPower = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getNoUidMask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getComponentHistory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getTotals = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getMeans = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getRuntime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getUidInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getUidExtra = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public java.lang.String[] getComponents() throws android.os.RemoteException;
public int[] getComponentsMaxPower() throws android.os.RemoteException;
public int getNoUidMask() throws android.os.RemoteException;
public int[] getComponentHistory(int count, int componentId, int uid) throws android.os.RemoteException;
// Returns the total energy consumption for each component in the same order
// that the components were returned in getComponents() and in the same order
// that the components are populated by PhoneSelector.generateComponents().
//
// uid can be specified to make the information specific to a single user
// id or SystemInfo.AID_ALL can be specified to request global information.
//
// windowType should be one of Counter.WINDOW_MINUTE, Counter.WINDOW_HOUR,
// Counter.WINDOW_DAY, Counter.WINDOW_TOTAL to request the window that the
// energy usage will be calculated over.
//
// The returned result is given in mJ.

public long[] getTotals(int uid, int windowType) throws android.os.RemoteException;
// Like getTotals() except that each entry is divided by how long the given
// uid was running.  If SystemInfo.AID_ALL is provided this is effectively
// like dividing each entry by the window size. (unless PowerTutor hasn't
// been running that long).

public long[] getMeans(int uid, int windowType) throws android.os.RemoteException;
// Gets the total time that this uid has been running in seconds.

public long getRuntime(int uid, int windowType) throws android.os.RemoteException;
// Returns a byte array representing a serialized array of UidInfo structures.
// See UidInfo.java for what information is given.  Note that members marked
// as transient are not filled in.
// Power contributions from component i will be dropped if the ith bit is set
// in ignoreMask.  Providing 0 for ignoreMask will give results for all
// components.
//
// Example Usage:
//   byte[] rawUidInfo = counterService.getUidInfo(windowType);
//   UidInfo[] uidInfos = (UidInfo[])new ObjectInputStream(
//       new ByteArrayInputStream(rawUidInfo)).readObject();

public byte[] getUidInfo(int windowType, int ignoreMask) throws android.os.RemoteException;
// Return miscellaneous data point for the passed uid.
// Current extras included:
//   OLEDSCORE

public long getUidExtra(java.lang.String name, int uid) throws android.os.RemoteException;
}
