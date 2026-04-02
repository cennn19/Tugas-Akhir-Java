-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 02, 2026 at 06:11 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lokost`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_atur_batas_aman` (IN `p_batas_aman` DECIMAL(10,2))   BEGIN
    UPDATE pengaturan SET batas_aman = p_batas_aman WHERE id = 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_atur_budget_global` (IN `p_limit_global` DECIMAL(10,2))   BEGIN
    UPDATE pengaturan SET batas_keseluruhan = p_limit_global WHERE id = 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_atur_budget_kategori` (IN `p_id_kategori` INT, IN `p_limit_baru` DECIMAL(10,2))   BEGIN
    UPDATE kategori SET batas_anggaran = p_limit_baru WHERE id = p_id_kategori;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_reset_seluruh_data` ()   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    -- 1. Hapus semua transaksi & reset auto increment
    DELETE FROM transaksi;
    ALTER TABLE transaksi AUTO_INCREMENT = 1;

    -- 2. Hapus target tabungan & uang masuk
    DELETE FROM target_tabungan;
    ALTER TABLE target_tabungan AUTO_INCREMENT = 1;
    DELETE FROM uang_masuk;
    ALTER TABLE uang_masuk AUTO_INCREMENT = 1;

    -- 3. Reset budget global & batas aman ke nol
    UPDATE pengaturan SET batas_keseluruhan = 0, batas_aman = 0 WHERE id = 1;

    -- 4. Reset semua batas kategori ke nol
    UPDATE kategori SET batas_anggaran = 0;
    
    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_tambah_pengeluaran` (IN `p_nama` VARCHAR(100), IN `p_nominal` DECIMAL(10,2), IN `p_kategori` INT)   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK; 
        RESIGNAL; 
    END;

    START TRANSACTION;
    INSERT INTO transaksi (nama_barang, nominal, tanggal, id_kategori) 
    VALUES (p_nama, p_nominal, CURDATE(), p_kategori);
    COMMIT;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `id` int NOT NULL,
  `nama_kategori` varchar(50) NOT NULL,
  `tipe_kategori` enum('Kebutuhan','Keinginan') NOT NULL,
  `batas_anggaran` decimal(10,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`id`, `nama_kategori`, `tipe_kategori`, `batas_anggaran`) VALUES
(1, 'Makanan & Minuman', 'Kebutuhan', 0.00),
(2, 'Listrik & Kos', 'Kebutuhan', 0.00),
(3, 'Skincare & Mandi', 'Kebutuhan', 0.00),
(4, 'Hiburan/Game', 'Keinginan', 0.00),
(5, 'Nongkrong/Cafe', 'Keinginan', 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `pengaturan`
--

CREATE TABLE `pengaturan` (
  `id` int NOT NULL,
  `batas_keseluruhan` decimal(10,2) NOT NULL DEFAULT '0.00',
  `batas_aman` decimal(10,2) DEFAULT '0.00',
  `nama_user` varchar(100) DEFAULT 'Pengguna Baru'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pengaturan`
--

INSERT INTO `pengaturan` (`id`, `batas_keseluruhan`, `batas_aman`, `nama_user`) VALUES
(1, 0.00, 0.00, 'iki');

-- --------------------------------------------------------

--
-- Table structure for table `stok_barang`
--

CREATE TABLE `stok_barang` (
  `id` int NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `kategori_barang` varchar(50) DEFAULT NULL,
  `jumlah` int NOT NULL DEFAULT '0',
  `satuan` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `stok_barang`
--

INSERT INTO `stok_barang` (`id`, `nama_barang`, `kategori_barang`, `jumlah`, `satuan`) VALUES
(4, 'Mie', 'Makanan Instan', 8, 'Pcs'),
(5, 'Sampo', 'Peralatan Mandi', 0, 'Pcs'),
(6, 'tisu', 'Lainnya', 0, 'Pcs'),
(7, 'Tisu', 'Lainnya', 0, 'Bungkus'),
(8, 'sabun', 'Lainnya', 0, 'Bungkus');

-- --------------------------------------------------------

--
-- Table structure for table `target_tabungan`
--

CREATE TABLE `target_tabungan` (
  `id` int NOT NULL,
  `nama_target` varchar(100) DEFAULT NULL,
  `harga` decimal(10,2) DEFAULT NULL,
  `target_waktu_hari` int DEFAULT NULL,
  `terkumpul` decimal(10,2) DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id` int NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `nominal` decimal(10,2) NOT NULL,
  `tanggal` date NOT NULL,
  `id_kategori` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id`, `nama_barang`, `nominal`, `tanggal`, `id_kategori`) VALUES
(1, 'Mie', 0.00, '2026-04-02', 1),
(2, 'Mie', 35000.00, '2026-04-02', 1),
(3, 'Sampo', 0.00, '2026-04-02', 1),
(4, 'Tisu', 0.00, '2026-04-02', 1);

-- --------------------------------------------------------

--
-- Table structure for table `uang_masuk`
--

CREATE TABLE `uang_masuk` (
  `id` int NOT NULL,
  `sumber` varchar(100) DEFAULT NULL,
  `nominal` decimal(10,2) DEFAULT NULL,
  `tanggal` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_ringkasan_pengeluaran`
-- (See below for the actual view)
--
CREATE TABLE `v_ringkasan_pengeluaran` (
`id` int
,`nama_kategori` varchar(50)
,`tipe_kategori` enum('Kebutuhan','Keinginan')
,`batas_anggaran` decimal(10,2)
,`total_pengeluaran` decimal(32,2)
,`sisa_anggaran` decimal(33,2)
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pengaturan`
--
ALTER TABLE `pengaturan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `stok_barang`
--
ALTER TABLE `stok_barang`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `target_tabungan`
--
ALTER TABLE `target_tabungan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_kategori` (`id_kategori`);

--
-- Indexes for table `uang_masuk`
--
ALTER TABLE `uang_masuk`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `pengaturan`
--
ALTER TABLE `pengaturan`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `stok_barang`
--
ALTER TABLE `stok_barang`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `target_tabungan`
--
ALTER TABLE `target_tabungan`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `uang_masuk`
--
ALTER TABLE `uang_masuk`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

--
-- Structure for view `v_ringkasan_pengeluaran`
--
DROP TABLE IF EXISTS `v_ringkasan_pengeluaran`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_ringkasan_pengeluaran`  AS SELECT `k`.`id` AS `id`, `k`.`nama_kategori` AS `nama_kategori`, `k`.`tipe_kategori` AS `tipe_kategori`, `k`.`batas_anggaran` AS `batas_anggaran`, coalesce(sum(`t`.`nominal`),0) AS `total_pengeluaran`, (`k`.`batas_anggaran` - coalesce(sum(`t`.`nominal`),0)) AS `sisa_anggaran` FROM (`kategori` `k` left join `transaksi` `t` on((`k`.`id` = `t`.`id_kategori`))) GROUP BY `k`.`id` ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
