require u-boot.inc

DESCRIPTION = "U-Boot-Hardkernel - git repo"
HOMEPAGE = "http://hardkernel.com"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://../git/COPYING;md5=1707d6db1d42237583f50183a5651ecb"

# from where to fetch the u-boot
UBOOT_REPO_URI ??= "git://github.com/hardkernel/u-boot.git"
UBOOT_BRANCH_odroid-c1 ?= "odroidc-v2011.03"
UBOOT_BRANCH_odroid-xu3 ?= "odroidxu3-v2012.07"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}" V=1'

SRC_URI = " \
  ${UBOOT_REPO_URI};branch=${UBOOT_BRANCH} \
"

SRC_URI_append_odroid-c1 = " \
  file://cross.patch \
  file://boot.ini \
"
SRC_URI_append_odroid-xu3 = " \
  file://boot-xu3.ini \
"

S = "${WORKDIR}/git"
SRCREV = "${AUTOREV}"

do_deploy_append () {
    install -d ${DEPLOYDIR}
    if [ "${MACHINE}" = "odroid-c1" ]
    then
        cp -v ${S}/sd_fuse/bl1.bin.hardkernel ${WORKDIR}/boot.ini ${DEPLOYDIR}
    elif [ "${MACHINE}" = "odroid-xu3" ]
    then
        for i in bl1 bl2 tzsw
        do
            cp -v ${S}/sd_fuse/hardkernel/${i}.bin.hardkernel ${DEPLOYDIR}
        done
        cp -v ${WORKDIR}/boot-xu3.ini ${DEPLOYDIR}/boot.ini
    fi
}

do_deploy_prepend_odroid-c1 () {
    ln -fs sd_fuse/u-boot.bin ${S}
}
