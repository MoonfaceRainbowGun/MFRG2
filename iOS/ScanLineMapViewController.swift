//
//  ScanLineMapViewController.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 2/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit

class ScanLineMapViewController: UIViewController {
    var newMap = ScanLineMapView()
    var oldMap = ScanLineMapView()
    var scanLineBackground = ScanLineBackgroundView()
    var scanLineMask = UIView()
    
    let mask = UIView()
    
    private var newMeasurements: [ScanLineMapView.Datasource.ActiveMeasurement]? = nil
    
    private var displayLink: CADisplayLink?
    private var start: CFAbsoluteTime?
    private let duration: TimeInterval = 3.0
    private var percent: Double = 0.0
    
    private var hasStarted = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureViews()
        configureConstraints()
        configureData()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        let imageView = UIImageView()
        imageView.image = #imageLiteral(resourceName: "mfrg-scan-line")
        scanLineBackground.updateContent(snapshotView: imageView)
    }
    
    func startDisplayLink() {
        displayLink = CADisplayLink(target: self, selector: #selector(handleDisplayLink(displayLink:)))
        start = CFAbsoluteTimeGetCurrent()
        displayLink?.add(to: .main, forMode: .commonModes)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        displayLink?.invalidate()
        displayLink = nil
    }
    
    func resetDisplayLink() {
        displayLink?.invalidate()
        displayLink = nil
        
        if let newMeasurements = self.newMeasurements {
            ageNewMap(newMeasurements: newMeasurements)
        } else {
            ageNewMap(newMeasurements: newMap.datasource?.activeBlockMeasurements ?? [])
        }
        
        startDisplayLink()
    }
    
    @objc func handleDisplayLink(displayLink: CADisplayLink) {
        newMap.isHidden = false
        let elapsed = CFAbsoluteTimeGetCurrent() - start!
        if elapsed >= duration {
            resetDisplayLink()
        }
        
        percent = elapsed / duration
        updateMask(progress: CGFloat(percent))
    }
}

extension ScanLineMapViewController {
    func configureViews() {
        view.backgroundColor = .white
        navigationController?.navigationBar.barStyle = .blackOpaque
        navigationController?.navigationBar.tintColor = Theme.qrScanner.navigationBarButtonColor
        navigationController?.navigationBar.setBackgroundImage(UIColor(hex: "2E4053")?.image()!, for: .default)
        
        
        do {
            let datasource = ScanLineMapView.Datasource(activeBlockMeasurements: [
//                .init(blockIndex: "A", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "B", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "C", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "D", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "E", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "F", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "G", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "H", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "I1", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "I2", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "J", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "K", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "L", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "M", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "N", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "O", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "P", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Q", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "R", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "S", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "T", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "U", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "V", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "W", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "X", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Y", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Z", crowdness: .AlreadyFuckedUp),
            ])
            oldMap.datasource = datasource
            view.addSubview(oldMap)
        }
        
        
        do {
            let datasource = ScanLineMapView.Datasource(activeBlockMeasurements: [
//                .init(blockIndex: "A", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "B", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "C", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "D", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "E", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "F", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "G", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "H", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "I1", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "I2", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "J", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "K", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "L", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "M", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "N", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "O", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "P", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Q", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "R", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "S", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "T", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "U", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "V", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "W", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "X", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Y", crowdness: .AlreadyFuckedUp),
//                .init(blockIndex: "Z", crowdness: .AlreadyFuckedUp),
            ])
            newMap.datasource = datasource
            view.addSubview(newMap)
        }
        
        do {
            view.addSubview(scanLineBackground)
            scanLineBackground.mask = scanLineMask
            scanLineMask.backgroundColor = .black
        }
        
        mask.backgroundColor = .black
        newMap.mask = mask
    }
    
    func configureConstraints() {
        newMap.snp.remakeConstraints { (make) in
            make.edges.equalToSuperview()
        }
        
        oldMap.snp.remakeConstraints { (make) in
            make.edges.equalToSuperview()
        }
        
        scanLineBackground.snp.remakeConstraints { (make) in
            make.edges.equalToSuperview()
        }
    }
    
    func updateMask(progress: CGFloat) {
        let height = view.bounds.height * progress
        mask.frame = CGRect(x: 0, y: 0, width: view.bounds.width, height: height)
        scanLineMask.frame = CGRect(x: 0, y: mask.frame.maxY, width: view.bounds.width, height: 10)
    }
    
    func ageNewMap(newMeasurements: [ScanLineMapView.Datasource.ActiveMeasurement]) {
        newMap.mask = nil
        
        let tempMap = oldMap
        oldMap = newMap
        newMap = tempMap
        
        mask.frame = .zero
        newMap.mask = mask
        
        newMap.datasource = ScanLineMapView.Datasource(activeBlockMeasurements: newMeasurements)
        scanLineBackground.updateContent(snapshotView: oldMap.snapshotView(afterScreenUpdates: false))
        
        newMap.isHidden = true
        view.bringSubview(toFront: newMap)
        view.bringSubview(toFront: scanLineBackground)
    }
    
    func configureData() {
        if #available(iOS 10.0, *) {
            Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { (timer) in
                ScanLineNetworkManager.getHeatMap(start: 0, end: 9999999999) { (measurements) in
                    DispatchQueue.main.async {
                        if !(self.hasStarted) {
                            self.hasStarted = true
                            let datasource = ScanLineMapView.Datasource(activeBlockMeasurements: measurements)
                            self.newMap.datasource = datasource
                            self.startDisplayLink()
                        } else {
                            self.newMeasurements = measurements
                        }
                    }
                }
            }
        } else {
            // Fallback on earlier versions
        }
    }
}
