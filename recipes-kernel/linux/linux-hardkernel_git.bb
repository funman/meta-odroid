DESCRIPTION = "Linux kernel for the Hardkernel ODROID-U2 device"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"


# Mark archs/machines that this kernel supports
COMPATIBLE_MACHINE_odroid_u2 = "odroid-u2"
COMPATIBLE_MACHINE_odroid_c1 = "odroid-c1"
COMPATIBLE_MACHINE_odroid_xu3 = "odroid-xu3"

inherit kernel siteinfo
require linux-dtb.inc

# from where to fetch the kernel
KERNEL_REPO_OWNER ??= "hardkernel"
KERNEL_REPO_URI ??= "git://github.com/${KERNEL_REPO_OWNER}/linux.git"
KBRANCH_odroid-c1 ?= "odroidc-3.10.y"
KBRANCH_odroid-xu3 ?= "odroidxu3-3.10.y"
KERNEL_DEVICETREE_odroid-c1 = "meson8b_odroidc.dtb"
KERNEL_DEFCONFIG_odroid-c1 = "odroidc_defconfig"
KERNEL_DEVICETREE_odroid-xu3 = "exynos5422-odroidxu3.dtb"
KERNEL_DEFCONFIG_odroid-xu3 = "odroidxu3_defconfig"

SRC_URI = " \
  ${KERNEL_REPO_URI};branch=${KBRANCH} \
"
SRC_URI_append_odroid-c1 = " \
  file://0001-Revert-broken-DTB-build-rules-changes.patch \
"

S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"

KV_odroid-c1 = "3.10.70"
KV_odroid-xu3 = "3.10.69"
PV = "${KV}+gitr${SRCPV}"
LOCALVERSION ?= ""

# stolen from meta-oe's linux.inc
#kernel_conf_variable CMDLINE "\"${CMDLINE} ${CMDLINE_DEBUG}\""
kernel_conf_variable() {
    CONF_SED_SCRIPT="$CONF_SED_SCRIPT /CONFIG_$1[ =]/d;"
    if test "$2" = "n"
    then
        echo "# CONFIG_$1 is not set" >> ${S}/.config
    else
        echo "CONFIG_$1=$2" >> ${S}/.config
    fi
}

do_configure_prepend() {
    install -m 0644 ${S}/arch/${ARCH}/configs/${KERNEL_DEFCONFIG} ${WORKDIR}/defconfig || die "No default configuration for ${MACHINE} / ${KERNEL_DEFCONFIG} available."
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
}

PACKAGES =+ "kernel-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"
