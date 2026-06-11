Add-Type -AssemblyName System.Drawing
$img = [System.Drawing.Image]::FromFile('C:\Users\leand\OneDrive\Documentos\Hytale mcp\Minecraft\CouplesMod\src\main\resources\assets\couplesmod\textures\gui\couples_menu.png')
Write-Host "$($img.Width)x$($img.Height)"
$img.Dispose()
