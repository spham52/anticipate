#include <iostream>
#include <string>
#include "wifiProvision.h"

using namespace std;

int main() {

   string wifiGatewayInfoPath = "/path/to/wifi/info";

   WifiProvisioner provisioner(wifiGatewayInfoPath);

    return 0;
}