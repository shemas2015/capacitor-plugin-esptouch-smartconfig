import Foundation

@objc public class EsptouchActivity: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
