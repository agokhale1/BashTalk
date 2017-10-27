;This file will be executed next to the application bundle image
;I.e. current directory will contain folder BashTalk Client with application files
[Setup]
AppId={{bashtalkclient.core}}
AppName=BashTalk Client
AppVersion=1.0.0
AppVerName=BashTalk Client 1.0.0
AppPublisher=BashTalk
AppComments=BashTalk Client
AppCopyright=Copyright (C) 2017
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\BashTalk Client
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=No
DisableWelcomePage=Yes
DefaultGroupName=BashTalk Client
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=BashTalk Client
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=BashTalk Client\BashTalk Client.ico
UninstallDisplayIcon={app}\BashTalk Client.ico
UninstallDisplayName=BashTalk Client
WizardImageStretch=No
WizardSmallImageFile=BashTalk Client-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "BashTalk Client\BashTalk Client.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "BashTalk Client\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BashTalk Client"; Filename: "{app}\BashTalk Client.exe"; IconFilename: "{app}\BashTalk Client.ico"; Check: returnTrue()
Name: "{commondesktop}\BashTalk Client"; Filename: "{app}\BashTalk Client.exe";  IconFilename: "{app}\BashTalk Client.ico"; Check: returnTrue()


[Run]
Filename: "{app}\BashTalk Client.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\BashTalk Client.exe"; Description: "{cm:LaunchProgram,BashTalk Client}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\BashTalk Client.exe"; Parameters: "-install -svcName ""BashTalk Client"" -svcDesc ""BashTalk Client"" -mainExe ""BashTalk Client.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\BashTalk Client.exe "; Parameters: "-uninstall -svcName BashTalk Client -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
